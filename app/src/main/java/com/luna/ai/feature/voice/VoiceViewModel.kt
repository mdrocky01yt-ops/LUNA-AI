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

    private val _lunaResponse = MutableStateFlow("I am ready. Speak now...")
    val lunaResponse: StateFlow<String> = _lunaResponse

    private val apiHistory = mutableListOf<ChatMessage>()

    init {
        viewModelScope.launch {
            // ভয়েস সেটিংস লোড
            val pitch = dataStoreManager.voicePitchFlow.first()
            val speed = dataStoreManager.voiceSpeedFlow.first()
            val name = dataStoreManager.voiceNameFlow.first()
            voiceManager.updateVoiceParameters(pitch, speed, name)

            // পার্সোনালিটি লোড
            val personality = dataStoreManager.aiPersonalityFlow.first()
            val systemPrompt = "$personality\n\nIMPORTANT: Keep answers short. You can control the phone.\nIf user asks to do something, reply EXACTLY like: ACTION:OPEN_APP:package.name OR ACTION:READ_SCREEN OR ACTION:CLICK_TEXT:text OR ACTION:SCROLL_DOWN OR ACTION:BACK OR ACTION:FLASHLIGHT OR ACTION:BATTERY"
            apiHistory.add(ChatMessage("system", systemPrompt))
        }

        // TTS শেষ হলে আবার মাইক চালু হবে (লুপ সিস্টেম)
        voiceManager.onTtsFinished = {
            startListening()
        }

        // অ্যাপ ওপেন হওয়া মাত্রই মাইক চালু হবে
        startListening()
    }

    fun startListening() {
        if (voiceManager.isSpeaking.value) return
        
        _lunaResponse.value = "Listening..."
        _userQuery.value = ""
        
        voiceManager.startListening(
            onResult = { text ->
                if (text.isNotBlank()) {
                    _userQuery.value = text
                    processQuery(text)
                }
            },
            onEnd = {
                // যদি কিছু না শোনে, আবার চেষ্টা করবে
                if (_lunaResponse.value == "Listening...") {
                    _lunaResponse.value = "Waiting for you to speak..."
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
            val result = repository.sendMessage(apiHistory)
            
            result.onSuccess { reply ->
                var finalReply = reply

                if (reply.contains("ACTION:")) {
                    val actionResult = actionParser.executeAction(reply)
                    finalReply = if (actionResult != null) actionResult else "দুঃখিত, কাজটি করা সম্ভব হয়নি।"
                }

                _lunaResponse.value = finalReply
                apiHistory.add(ChatMessage("assistant", reply))
                voiceManager.speak(finalReply) // কথা বলা শেষ হলে onTtsFinished কল হবে
                
            }.onFailure { error ->
                _lunaResponse.value = "Error: ${error.message}"
                // এরর হলেও আবার শোনা শুরু করবে
                startListening()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
