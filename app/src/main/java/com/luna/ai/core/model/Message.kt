package com.luna.ai.core.model

data class Message(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
