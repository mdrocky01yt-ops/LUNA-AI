package com.luna.ai.feature.silent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.utils.PromptHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SilentViewModel @Inject constructor(
    private val repository: AiRepository,
    private val actionParser: ActionParser
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val apiHistory = mutableListOf(
        ChatMessage("system", PromptHelper.getSystemPrompt())
    )

    init {
        _messages.value = _messages.value + ("হ্যালো! আমি লুনা। আপনি এখন টাইপ করে আমাকে কমান্ড দিতে পারেন।" to false)
    }

    fun sendCommand(userText: String) {
        if (userText.isBlank()) return

        _messages.value = _messages.value + (userText to true)
        apiHistory.add(ChatMessage("user", userText))
        _isLoading.value = true

        viewModelScope.launch {
            // এখানে "DeepSeek" পাস করা হলো
            val result = repository.sendMessage(apiHistory, "DeepSeek")
            
            _isLoading.value = false
            
            result.onSuccess { reply ->
                var finalReply = reply
                
                if (reply.contains("ACTION:")) {
                    val actionExecuted = actionParser.executeAction(reply)
                    if (actionExecuted != null) {
                        finalReply = "✅ $actionExecuted"
                    } else {
                        finalReply = "❌ দুঃখিত, আমি কাজটি করতে পারিনি।"
                    }
                }

                _messages.value = _messages.value + (finalReply to false)
                apiHistory.add(ChatMessage("assistant", reply))
                
            }.onFailure { error ->
                _messages.value = _messages.value + ("Error: ${error.message}" to false)
            }
        }
    }
}
