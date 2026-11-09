package com.luna.ai.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.model.Message
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
    private val chatDao: ChatDao
) : ViewModel() {

    // ডাটাবেস থেকে মেসেজ লোড করা
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
        apiHistory.add(ChatMessage("system", "You are Luna, a helpful and friendly AI assistant. You reply in Bengali if the user speaks in Bengali."))
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            // ১. ইউজারের মেসেজ ডাটাবেসে সেভ
            val userMsg = Message(text = userText, isFromUser = true)
            chatDao.insertMessage(userMsg)

            // ২. হিস্ট্রিতে যোগ করা
            apiHistory.add(ChatMessage("user", userText))

            // ৩. লোডিং শুরু
            _isLoading.value = true

            // ৪. API কল
            val result = repository.sendMessage(apiHistory)
            
            _isLoading.value = false
            
            result.onSuccess { reply ->
                // লুনার উত্তর ডাটাবেসে সেভ
                val lunaMsg = Message(text = reply, isFromUser = false)
                chatDao.insertMessage(lunaMsg)
                apiHistory.add(ChatMessage("assistant", reply))
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
            apiHistory.add(ChatMessage("system", "You are Luna, a helpful and friendly AI assistant. You reply in Bengali if the user speaks in Bengali."))
        }
    }
}
