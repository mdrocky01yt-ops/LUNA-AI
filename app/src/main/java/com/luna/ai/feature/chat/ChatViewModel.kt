
package com.luna.ai.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.model.Message
import com.luna.ai.core.utils.PromptHelper
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val repository: AiRepository,
    private val chatDao: ChatDao,
    private val actionParser: ActionParser,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val messages: StateFlow<List<Message>> =
        chatDao.getAllMessages().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(
        userText: String,
        base64Image: String?
    ) {

        if (_isLoading.value) return

        if (userText.isBlank() && base64Image.isNullOrBlank()) {
            return
        }

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val finalUserText =
                    if (userText.isBlank()) {
                        "এই ছবিতে কী আছে বুঝিয়ে বলো।"
                    } else {
                        userText.trim()
                    }

                // 1. User message Database-এ সংরক্ষণ
                val userMessage = Message(
                    text = finalUserText,
                    isFromUser = true,
                    imageUrl = if (base64Image != null) {
                        "image_attached"
                    } else {
                        null
                    }
                )

                chatDao.insertMessage(userMessage)

                // 2. Database থেকে সম্পূর্ণ chat history নেওয়া
                val savedMessages = chatDao.getAllMessages().first()

                // 3. System prompt তৈরি
                val personality =
                    dataStoreManager.aiPersonalityFlow.first()

                val systemPrompt =
                    PromptHelper.getSystemPrompt(personality)

                // 4. Database messages Gemini format-এ রূপান্তর
                val conversationHistory = savedMessages
                    .filterNot {
                        !it.isFromUser &&
                        it.text.startsWith("Error:")
                    }
                    .takeLast(40)
                    .map { message ->

                        ChatMessage(
                            role = if (message.isFromUser) {
                                "user"
                            } else {
                                "assistant"
                            },
                            content = message.text
                        )
                    }

                // 5. System instruction প্রথমে যুক্ত
                val completeHistory = listOf(
                    ChatMessage(
                        role = "system",
                        content = systemPrompt
                    )
                ) + conversationHistory

                // 6. Gemini-তে সম্পূর্ণ history পাঠানো
                val result = repository.sendMessage(
                    history = completeHistory,
                    imageBase64 = base64Image
                )

                result.onSuccess { reply ->

                    var finalText = reply
                    var generatedImageUrl: String? = null

                    // 7. AI action পরীক্ষা
                    if (reply.contains("ACTION:")) {

                        if (reply.contains(
                                "ACTION:GENERATE_IMAGE:"
                            )
                        ) {

                            val prompt = reply
                                .substringAfter(
                                    "ACTION:GENERATE_IMAGE:"
                                )
                                .trim()

                            val encodedPrompt =
                                URLEncoder.encode(
                                    prompt,
                                    "UTF-8"
                                )

                            generatedImageUrl =
                                "https://image.pollinations.ai/prompt/$encodedPrompt"

                            finalText =
                                "আপনার ছবির অনুরোধ প্রস্তুত করা হয়েছে।"

                        } else {

                            val actionResult =
                                actionParser.executeAction(reply)

                            finalText =
                                if (actionResult != null) {
                                    "✅ $actionResult"
                                } else {
                                    "❌ কাজটি সম্পন্ন করা যায়নি। প্রয়োজনীয় অনুমতি অথবা সুবিধা পরীক্ষা করুন।"
                                }
                        }
                    }

                    // 8. AI response Database-এ সংরক্ষণ
                    val assistantMessage = Message(
                        text = finalText,
                        isFromUser = false,
                        imageUrl = generatedImageUrl
                    )

                    chatDao.insertMessage(assistantMessage)
                }

                result.onFailure { error ->

                    // Error UI-তে দেখানোর জন্য সংরক্ষণ
                    chatDao.insertMessage(
                        Message(
                            text = "Error: ${error.message}",
                            isFromUser = false
                        )
                    )
                }

            } catch (e: Exception) {

                chatDao.insertMessage(
                    Message(
                        text = "Error: ${e.localizedMessage}",
                        isFromUser = false
                    )
                )

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun clearHistory() {

        viewModelScope.launch {

            chatDao.clearHistory()
        }
    }
}
