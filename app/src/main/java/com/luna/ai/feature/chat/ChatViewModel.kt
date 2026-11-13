package com.luna.ai.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AiRepository,
    private val chatDao: ChatDao,
    private val actionParser: ActionParser,
    private val dataStoreManager: DataStoreManager
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
        // অ্যাপ ওপেন হওয়ার সময় সেভ করা পার্সোনালিটি লোড করবে
        viewModelScope.launch {
            val personality = dataStoreManager.aiPersonalityFlow.first()
            // অ্যাকশন কমান্ডের নিয়ম + সেভ করা পার্সোনালিটি একসাথে সেট করা হলো
            val systemPrompt = "$personality\n\nIMPORTANT: You can control the Android phone.\nIf user asks to do something on phone, reply EXACTLY like: ACTION:OPEN_APP:package.name OR ACTION:FLASHLIGHT OR ACTION:VOLUME_UP OR ACTION:VOLUME_DOWN"
            apiHistory.add(ChatMessage("system", systemPrompt))
        }
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
                
                if (reply.contains("ACTION:")) {
                    val actionExecuted = actionParser.executeAction(reply)
                    finalReply = if (actionExecuted) "✅ কাজটি সম্পন্ন করা হয়েছে।" else "❌ দুঃখিত, কাজটি করা সম্ভব হয়নি।"
                }

                val lunaMsg = Message(text = finalReply, isFromUser = false)
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
            val personality = dataStoreManager.aiPersonalityFlow.first()
            val systemPrompt = "$personality\n\nIMPORTANT: You can control the Android phone.\nIf user asks to do something on phone, reply EXACTLY like: ACTION:OPEN_APP:package.name OR ACTION:FLASHLIGHT OR ACTION:VOLUME_UP OR ACTION:VOLUME_DOWN"
            apiHistory.add(ChatMessage("system", systemPrompt))
        }
    }
}
