package com.luna.ai.ai.repository

import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.model.DeepSeekRequest
import com.luna.ai.ai.model.GeminiContent
import com.luna.ai.ai.model.GeminiPart
import com.luna.ai.ai.model.GeminiRequest
import com.luna.ai.ai.network.DeepSeekService
import com.luna.ai.ai.network.GeminiService
import com.luna.ai.core.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val deepSeekService: DeepSeekService,
    private val geminiService: GeminiService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun sendMessage(history: List<ChatMessage>, provider: String): Result<String> {
        if (provider == "Gemini") {
            // Gemini-র সব Key ট্রাই করবে
            val geminiKeys = dataStoreManager.geminiKeysFlow.first()
            val lastUserMessage = history.lastOrNull { it.role == "user" }?.content ?: ""
            
            for (key in geminiKeys) {
                try {
                    val request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = lastUserMessage))))
                    )
                    val response = geminiService.generateContent(apiKey = key, request = request)
                    val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!reply.isNullOrBlank()) {
                        return Result.success(reply)
                    }
                } catch (e: Exception) { }
            }
            return Result.failure(Exception("Gemini API Key সঠিক নয় বা লিমিট শেষ। দয়া করে API KEYS-এ গিয়ে চেক করুন।"))
        } else {
            // DeepSeek-এর সব Key ট্রাই করবে
            val deepSeekKeys = dataStoreManager.deepSeekKeysFlow.first()
            for (key in deepSeekKeys) {
                try {
                    val request = DeepSeekRequest(messages = history)
                    val response = deepSeekService.sendMessage("Bearer $key", request)
                    val reply = response.choices.firstOrNull()?.message?.content
                    if (!reply.isNullOrBlank()) {
                        return Result.success(reply)
                    }
                } catch (e: Exception) { }
            }
            return Result.failure(Exception("DeepSeek API Key সঠিক নয় বা লিমিট শেষ। দয়া করে API KEYS-এ গিয়ে চেক করুন।"))
        }
    }
}
