package com.luna.ai.ai.model

import com.google.gson.annotations.SerializedName

data class DeepSeekRequest(
    @SerializedName("model") val model: String = "deepseek-chat",
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Float = 0.7f
)

data class ChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class DeepSeekResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("message") val message: ChatMessage
)
