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

    private val _lunaResponse = MutableStateFlow("Tap the mic to start speaking...")
    val lunaResponse: StateFlow<String> = _lunaResponse

    private val apiHistory = mutableListOf<ChatMessage>()

    init {
        viewModelScope.launch {
            // ভয়েস সেটিংস লোড
            val pitch = dataStoreManager.voicePitchFlow.first()
            val speed = dataStoreManager.voiceSpeedFlow.first()
            voiceManager.updateVoiceParameters(pitch, speed)

            // পার্সোনালিটি লোড
            val personality = dataStoreManager.aiPersonalityFlow.first()
            val systemPrompt = "$personality\n\nIMPORTANT: Keep your answers short and conversational. You can control the Android phone.\nIf user asks to do something on phone, reply EXACTLY like: ACTION:OPEN_APP:package.name OR ACTION:FLASHLIGHT OR ACTION:VOLUME_UP OR ACTION:VOLUME_DOWN"
            apiHistory.add(ChatMessage("system", systemPrompt))
        }
    }

    fun startListening() {
        voiceManager.stopSpeaking()
        _userQuery.value = ""
        _lunaResponse.value = "Listening..."
        
        voiceManager.startListening(
            onResult = { text ->
                _userQuery.value = text
                processQuery(text)
            },
            onEnd = {
                if (_userQuery.value.isEmpty()) {
                    _lunaResponse.value = "Tap the mic to start speaking..."
                }
            }
        )
    }

    fun stopListening() {
        voiceManager.stopListening()
    }

    private fun processQuery(text: String) {
        _lunaResponse.value = "Thinking..."
        apiHistory.add(ChatMessage("user", text))

        viewModelScope.launch {
            val result = repository.sendDeepSeekMessage(apiHistory)
            
            result.onSuccess { reply ->
                var finalReply = reply

                if (reply.contains("ACTION:")) {
                    val actionExecuted = actionParser.executeAction(reply)
                    finalReply = if (actionExecuted) "ঠিক আছে, কাজটি করে দিয়েছি।" else "দুঃখিত, কাজটি করা সম্ভব হয়নি।"
                }

                _lunaResponse.value = finalReply
                apiHistory.add(ChatMessage("assistant", reply))
                voiceManager.speak(finalReply)
                
            }.onFailure { error ->
                _lunaResponse.value = "Error: ${error.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
