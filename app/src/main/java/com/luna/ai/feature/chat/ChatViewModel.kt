package com.luna.ai.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AiRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(
        listOf(Message("1", "হ্যালো! আমি লুনা। আজ আমি আপনাকে কীভাবে সাহায্য করতে পারি?", false))
    )
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // API-তে পাঠানোর জন্য হিস্ট্রি
    private val apiHistory = mutableListOf<ChatMessage>()
    
    init {
        apiHistory.add(ChatMessage("system", "You are Luna, a helpful and friendly AI assistant. You reply in Bengali if the user speaks in Bengali."))
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        // ১. ইউজারের মেসেজ UI-তে দেখানো
        val userMsg = Message(System.currentTimeMillis().toString(), userText, true)
        _messages.value = _messages.value + userMsg

        // ২. হিস্ট্রিতে যোগ করা
        apiHistory.add(ChatMessage("user", userText))

        // ৩. লোডিং শুরু
        _isLoading.value = true

        // ৪. API কল
        viewModelScope.launch {
            val result = repository.sendMessage(apiHistory)
            
            _isLoading.value = false
            
            result.onSuccess { reply ->
                val lunaMsg = Message(System.currentTimeMillis().toString(), reply, false)
                _messages.value = _messages.value + lunaMsg
                apiHistory.add(ChatMessage("assistant", reply))
            }.onFailure { error ->
                val errorMsg = Message(System.currentTimeMillis().toString(), "Error: ${error.message}", false)
                _messages.value = _messages.value + errorMsg
            }
        }
    }
}
