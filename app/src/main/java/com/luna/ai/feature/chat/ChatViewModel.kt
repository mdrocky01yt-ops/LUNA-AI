package com.luna.ai.feature.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
        viewModelScope.launch {
            val personality = dataStoreManager.aiPersonalityFlow.first()
            val systemPrompt = "$personality\n\nIMPORTANT: You can control the Android phone using Accessibility Service.\nIf user asks to do something on phone, reply EXACTLY like: ACTION:OPEN_APP:package.name OR ACTION:READ_SCREEN OR ACTION:CLICK_TEXT:text OR ACTION:SCROLL_DOWN OR ACTION:BACK OR ACTION:FLASHLIGHT OR ACTION:BATTERY OR ACTION:BRIGHTNESS_UP OR ACTION:BRIGHTNESS_DOWN OR ACTION:READ_NOTIFICATIONS\n\nTo generate an image, reply EXACTLY: ACTION:GENERATE_IMAGE:prompt in English"
            apiHistory.add(ChatMessage("system", systemPrompt))
        }
    }

    fun sendMessage(userText: String, base64Image: String?) {
        if (userText.isBlank() && base64Image == null) return

        viewModelScope.launch {
            val finalUserText = if (userText.isBlank()) "এই ছবিতে কী আছে বুঝিয়ে বলো।" else userText
            val userMsg = Message(text = finalUserText, isFromUser = true, imageUrl = if (base64Image != null) "image_attached" else null)
            chatDao.insertMessage(userMsg)

            apiHistory.add(ChatMessage("user", finalUserText))
            _isLoading.value = true

            // এখানে আর "Gemini" প্যারামিটার নেই
            val result = repository.sendMessage(apiHistory, base64Image)
            
            _isLoading.value = false
            
            result.onSuccess { reply ->
                var finalText = reply
                var generatedImageUrl: String? = null
                
                if (reply.contains("ACTION:")) {
                    if (reply.contains("ACTION:GENERATE_IMAGE:")) {
                        val prompt = reply.substringAfter("ACTION:GENERATE_IMAGE:").trim()
                        val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
                        generatedImageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt"
                        finalText = "এই যে আপনার ছবি, তৈরি করে দিয়েছি! 🎨"
                    } else {
                        val actionResult = actionParser.executeAction(reply)
                        finalText = if (actionResult != null) "✅ $actionResult" else "❌ দুঃখিত, কাজটি করা সম্ভব হয়নি।"
                    }
                }

                val lunaMsg = Message(text = finalText, isFromUser = false, imageUrl = generatedImageUrl)
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
            val systemPrompt = "$personality\n\nIMPORTANT: You can control the Android phone using Accessibility Service.\nIf user asks to do something on phone, reply EXACTLY like: ACTION:OPEN_APP:package.name OR ACTION:READ_SCREEN OR ACTION:CLICK_TEXT:text OR ACTION:SCROLL_DOWN OR ACTION:BACK OR ACTION:FLASHLIGHT OR ACTION:BATTERY OR ACTION:BRIGHTNESS_UP OR ACTION:BRIGHTNESS_DOWN OR ACTION:READ_NOTIFICATIONS\n\nTo generate an image, reply EXACTLY: ACTION:GENERATE_IMAGE:prompt in English"
            apiHistory.add(ChatMessage("system", systemPrompt))
        }
    }
}
