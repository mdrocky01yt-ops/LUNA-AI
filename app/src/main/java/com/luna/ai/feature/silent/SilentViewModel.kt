
package com.luna.ai.feature.silent

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SilentViewModel @Inject constructor(
    private val repository: AiRepository,
    private val actionParser: ActionParser,
    private val chatDao: ChatDao,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _messages =
        MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            try {
                val savedMessages = chatDao.getAllMessages().first()

                _messages.value = if (savedMessages.isEmpty()) {
                    listOf(
                        "হ্যালো! আমি লুনা। আপনি এখন টাইপ করে আমাকে কমান্ড দিতে পারেন।" to false
                    )
                } else {
                    savedMessages.map { message ->
                        message.text to message.isFromUser
                    }
                }
            } catch (error: Exception) {
                _messages.value = listOf(
                    "হ্যালো! আমি লুনা। কিছু জিজ্ঞেস করুন।" to false
                )
            }
        }
    }

    fun sendCommand(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val cleanText = userText.trim()

        _messages.value = _messages.value + (cleanText to true)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                chatDao.insertMessage(
                    Message(
                        text = cleanText,
                        isFromUser = true
                    )
                )

                val personality =
                    dataStoreManager.aiPersonalityFlow.first()

                val history = mutableListOf<ChatMessage>()

                history.add(
                    ChatMessage(
                        role = "system",
                        content = PromptHelper.getSystemPrompt(personality)
                    )
                )

                val savedMessages = chatDao.getAllMessages().first()

                savedMessages
                    .takeLast(40)
                    .filterNot { message ->
                        !message.isFromUser &&
                            message.text.startsWith("Error:")
                    }
                    .forEach { message ->
                        history.add(
                            ChatMessage(
                                role = if (message.isFromUser) {
                                    "user"
                                } else {
                                    "assistant"
                                },
                                content = message.text
                            )
                        )
                    }

                val result = repository.sendMessage(history)

                result.onSuccess { reply ->
                    val finalReply = if (reply.contains("ACTION:")) {
                        actionParser.executeAction(reply)
                            ?: "দুঃখিত, আমি কাজটি সম্পন্ন করতে পারিনি।"
                    } else {
                        reply
                    }

                    chatDao.insertMessage(
                        Message(
                            text = finalReply,
                            isFromUser = false
                        )
                    )

                    _messages.value =
                        _messages.value + (finalReply to false)
                }.onFailure { error ->
                    val errorText =
                        "দুঃখিত, সমস্যা হয়েছে: ${error.message ?: "অজানা সমস্যা"}"

                    _messages.value =
                        _messages.value + (errorText to false)
                }

            } catch (error: Exception) {
                val errorText =
                    "দুঃখিত, সমস্যা হয়েছে: ${error.message ?: "অজানা সমস্যা"}"

                _messages.value =
                    _messages.value + (errorText to false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
