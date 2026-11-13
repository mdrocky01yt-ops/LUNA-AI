package com.luna.ai.feature.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.voice.VoiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceViewModel @Inject constructor(
    application: Application,
    private val repository: AiRepository
) : AndroidViewModel(application) {

    private val voiceManager = VoiceManager(application)

    val isListening = voiceManager.isListening
    val isSpeaking = voiceManager.isSpeaking

    private val _userQuery = MutableStateFlow("")
    val userQuery: StateFlow<String> = _userQuery

    private val _lunaResponse = MutableStateFlow("Tap the mic to start speaking...")
    val lunaResponse: StateFlow<String> = _lunaResponse

    private val apiHistory = mutableListOf(
        ChatMessage("system", "You are Luna, a helpful AI assistant. Keep your answers short and conversational.")
    )

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
            // এখানে ফাংশনের নাম আপডেট করা হলো
            val result = repository.sendDeepSeekMessage(apiHistory)
            
            result.onSuccess { reply ->
                _lunaResponse.value = reply
                apiHistory.add(ChatMessage("assistant", reply))
                voiceManager.speak(reply)
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
