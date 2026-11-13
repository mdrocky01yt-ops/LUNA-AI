package com.luna.ai.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.model.Message
import com.luna.ai.core.utils.PromptHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AiRepository,
    private val chatDao: ChatDao,
    private val actionParser: ActionParser // নতুন যুক্ত হলো
) : ViewModel() {

    val messages: StateFlow<List<Message>> = chatDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val apiHistory = mutableListOf<ChatMessage>()

    init {
        // এআই-কে অ্যাকশন নেওয়ার নিয়ম শেখানো হলো
        apiHistory.add(ChatMessage("system", PromptHelper.getSystemPrompt()))
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            val userMsg = Message(text = userText, isFromUser = true)
            chatDao.insertMessage(userMsg)

            apiHistory.add(ChatMessage("user", userText))
            _isLoading.value = true

            val result = repository.sendDeepSeekMessage(apiHistory)
            
            _isLoading.value = false
            
            result.onSuccess { reply ->
                var finalReply = reply
                
                // এআই যদি অ্যাকশন কমান্ড দেয়, তবে তা এক্সিকিউট করা হবে
                if (reply.contains("ACTION:")) {
                    val actionExecuted = actionParser.executeAction(reply)
                    if (actionExecuted) {
                        finalReply = "✅ কাজটি সম্পন্ন করা হয়েছে।"
                    } else {
                        finalReply = "❌ দুঃখিত, আমি কাজটি করতে পারিনি।"
                    }
                }

                val lunaMsg = Message(text = finalReply, isFromUser = false)
                chatDao.insertMessage(lunaMsg)
                apiHistory.add(ChatMessage("assistant", reply)) // হিস্ট্রিতে অরিজিনাল রিপ্লাই সেভ হবে
            }.onFailure { error ->
                val errorMsg = Message(text = "Error: ${error.message}", isFromUser = false)
                chatDao.insertMessage(errorMsg)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatDao.clearHistory()
            apiHistory.clear()
            apiHistory.add(ChatMessage("system", PromptHelper.getSystemPrompt()))
        }
    }
}
