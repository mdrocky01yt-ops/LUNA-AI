
package com.luna.ai.feature.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.model.Message
import com.luna.ai.core.utils.PromptHelper
import com.luna.ai.voice.VoiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceViewModel @Inject constructor(
    application: Application,
    private val repository: AiRepository,
    private val actionParser: ActionParser,
    private val dataStoreManager: DataStoreManager,
    private val chatDao: ChatDao
) : AndroidViewModel(application) {

    private val voiceManager = VoiceManager(application)

    val isListening: StateFlow<Boolean> = voiceManager.isListening
    val isSpeaking: StateFlow<Boolean> = voiceManager.isSpeaking

    private val _userQuery = MutableStateFlow("")
    val userQuery: StateFlow<String> = _userQuery

    private val _lunaResponse =
        MutableStateFlow("আমি প্রস্তুত। কথা বলতে মাইক বাটনে চাপ দিন।")
    val lunaResponse: StateFlow<String> = _lunaResponse

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private var shouldContinueListening = false

    init {
        viewModelScope.launch {
            val pitch = dataStoreManager.voicePitchFlow.first()
            val speed = dataStoreManager.voiceSpeedFlow.first()
            val name = dataStoreManager.voiceNameFlow.first()

            voiceManager.updateVoiceParameters(pitch, speed, name)
        }

        voiceManager.onTtsFinished = {
            if (shouldContinueListening) {
                startListening()
            }
        }
    }

    fun startListening() {
        if (_isProcessing.value || voiceManager.isSpeaking.value) return

        shouldContinueListening = true
        _lunaResponse.value = "Listening..."
        _userQuery.value = ""

        voiceManager.startListening(
            onResult = { recognizedText ->
                if (recognizedText.isNotBlank()) {
                    _userQuery.value = recognizedText
                    processQuery(recognizedText)
                }
            },
            onEnd = {
                if (_lunaResponse.value == "Listening...") {
                    _lunaResponse.value = "আবার কথা বলতে মাইক বাটনে চাপ দিন।"
                }
            }
        )
    }

    fun stopListening() {
        shouldContinueListening = false
        voiceManager.stopListening()
        voiceManager.stopSpeaking()
        _lunaResponse.value = "Voice Assistant বন্ধ করা হয়েছে।"
    }

    private fun processQuery(text: String) {
        if (_isProcessing.value) return

        shouldContinueListening = true
        _isProcessing.value = true
        _lunaResponse.value = "Thinking..."

        viewModelScope.launch {
            try {
                chatDao.insertMessage(
                    Message(
                        text = text,
                        isFromUser = true
                    )
                )

                val personality = dataStoreManager.aiPersonalityFlow.first()
                val systemPrompt = PromptHelper.getSystemPrompt(personality)

                val savedMessages = chatDao.getAllMessages().first()

                val history = mutableListOf<ChatMessage>()
                history.add(ChatMessage("system", systemPrompt))

                savedMessages
                    .takeLast(40)
                    .filterNot {
                        !it.isFromUser && it.text.startsWith("Error:")
                    }
                    .forEach { message ->
                        history.add(
                            ChatMessage(
                                role = if (message.isFromUser) "user" else "assistant",
                                content = message.text
                            )
                        )
                    }

                val result = repository.sendMessage(history)

                result.onSuccess { reply ->
                    var finalReply = reply

                    if (reply.contains("ACTION:")) {
                        val actionResult = actionParser.executeAction(reply)
                        finalReply = actionResult
                            ?: "দুঃখিত, এই কাজটি সম্পন্ন করা সম্ভব হয়নি।"
                    }

                    chatDao.insertMessage(
                        Message(
                            text = finalReply,
                            isFromUser = false
                        )
                    )

                    _lunaResponse.value = finalReply
                    voiceManager.speak(finalReply)
                }.onFailure { error ->
                    val errorText =
                        "দুঃখিত, সমস্যা হয়েছে: ${error.message ?: "অজানা সমস্যা"}"

                    _lunaResponse.value = errorText

                    chatDao.insertMessage(
                        Message(
                            text = "Error: ${error.message ?: "Unknown error"}",
                            isFromUser = false
                        )
                    )

                    voiceManager.speak(errorText)
                }
            } catch (error: Exception) {
                val errorText =
                    "দুঃখিত, সমস্যা হয়েছে: ${error.message ?: "অজানা সমস্যা"}"

                _lunaResponse.value = errorText
                voiceManager.speak(errorText)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    override fun onCleared() {
        shouldContinueListening = false
        voiceManager.destroy()
        super.onCleared()
    }
}
