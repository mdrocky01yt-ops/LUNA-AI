
package com.luna.ai.feature.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.core.device.ActionParser
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
    private val dataStoreManager: DataStoreManager
) : AndroidViewModel(application) {

    private val voiceManager = VoiceManager(application)

    val isListening = voiceManager.isListening
    val isSpeaking = voiceManager.isSpeaking

    private val _userQuery = MutableStateFlow("")
    val userQuery: StateFlow<String> = _userQuery

    private val _lunaResponse = MutableStateFlow("আমি প্রস্তুত। মাইক্রোফোনে চাপ দিয়ে কথা বলুন।")
    val lunaResponse: StateFlow<String> = _lunaResponse

    private val apiHistory = mutableListOf<ChatMessage>()

    private var isConfigurationReady = false
    private var pendingStartRequest = false
    private var voiceSessionActive = false

    init {
        viewModelScope.launch {
            try {
                val pitch = dataStoreManager.voicePitchFlow.first()
                val speed = dataStoreManager.voiceSpeedFlow.first()
                val name = dataStoreManager.voiceNameFlow.first()

                voiceManager.updateVoiceParameters(pitch, speed, name)

                val personality = dataStoreManager.aiPersonalityFlow.first()
                val systemPrompt = """
                    $personality

                    IMPORTANT:
                    - Keep answers short and useful.
                    - Reply in Bengali when appropriate.
                    - Do not claim an action was completed unless it was actually performed.
                    - For supported phone actions, reply using exactly one of these formats:
                      ACTION:OPEN_APP:package.name
                      ACTION:READ_SCREEN
                      ACTION:CLICK_TEXT:text
                      ACTION:SCROLL_DOWN
                      ACTION:SCROLL_UP
                      ACTION:BACK
                      ACTION:FLASHLIGHT
                      ACTION:BATTERY
                      ACTION:BRIGHTNESS_UP
                      ACTION:BRIGHTNESS_DOWN
                      ACTION:READ_NOTIFICATIONS
                """.trimIndent()

                apiHistory.clear()
                apiHistory.add(ChatMessage("system", systemPrompt))

                isConfigurationReady = true

                if (pendingStartRequest) {
                    pendingStartRequest = false
                    startListeningNow()
                }
            } catch (error: Exception) {
                _lunaResponse.value =
                    "ভয়েস সেটিংস লোড করা যায়নি: ${error.message ?: "অজানা সমস্যা"}"
            }
        }

        voiceManager.onTtsFinished = {
            if (voiceSessionActive) {
                startListeningNow()
            }
        }
    }

    fun startListening() {
        voiceSessionActive = true

        if (!isConfigurationReady) {
            pendingStartRequest = true
            _lunaResponse.value = "ভয়েস প্রস্তুত হচ্ছে..."
            return
        }

        startListeningNow()
    }

    private fun startListeningNow() {
        if (!voiceSessionActive || voiceManager.isSpeaking.value) return

        _lunaResponse.value = "Listening..."
        _userQuery.value = ""

        voiceManager.startListening(
            onResult = { recognizedText ->
                if (recognizedText.isNotBlank() && voiceSessionActive) {
                    _userQuery.value = recognizedText.trim()
                    processQuery(recognizedText.trim())
                }
            },
            onEnd = {
                if (voiceSessionActive &&
                    _lunaResponse.value == "Listening..."
                ) {
                    _lunaResponse.value = "আপনার কথা শোনার অপেক্ষায়..."
                }
            }
        )
    }

    fun stopListening() {
        voiceSessionActive = false
        pendingStartRequest = false
        voiceManager.stopListening()
        voiceManager.stopSpeaking()
        _lunaResponse.value = "ভয়েস বন্ধ করা হয়েছে।"
    }

    private fun processQuery(text: String) {
        if (!voiceSessionActive) return

        _lunaResponse.value = "Thinking..."
        apiHistory.add(ChatMessage("user", text))

        viewModelScope.launch {
            val result = repository.sendMessage(apiHistory.toList())

            result.onSuccess { reply ->
                var finalReply = reply

                if (reply.contains("ACTION:")) {
                    finalReply = actionParser.executeAction(reply)
                        ?: "দুঃখিত, এই কাজটি সম্পন্ন করা সম্ভব হয়নি।"
                }

                apiHistory.add(ChatMessage("assistant", reply))
                _lunaResponse.value = finalReply

                if (voiceSessionActive) {
                    voiceManager.speak(finalReply)
                }
            }.onFailure { error ->
                _lunaResponse.value =
                    "সমস্যা হয়েছে: ${error.message ?: "আবার চেষ্টা করুন।"}"
            }
        }
    }

    override fun onCleared() {
        voiceSessionActive = false
        pendingStartRequest = false
        voiceManager.destroy()
        super.onCleared()
    }
}
