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
    // ১. DeepSeek দিয়ে চ্যাট
    suspend fun sendDeepSeekMessage(history: List<ChatMessage>): Result<String> {
        return try {
            val apiKey = dataStoreManager.deepSeekKeysFlow.first().firstOrNull() ?: ""
            if (apiKey.isBlank()) {
                return Result.failure(Exception("DeepSeek API Key সেট করা হয়নি। Menu -> API KEYS-এ যান।"))
            }

            val request = DeepSeekRequest(messages = history)
            val response = deepSeekService.sendMessage(
                auth = "Bearer $apiKey",
                request = request
            )
            
            val reply = response.choices.firstOrNull()?.message?.content ?: "No response."
            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ২. Gemini দিয়ে চ্যাট
    suspend fun sendGeminiMessage(prompt: String): Result<String> {
        return try {
            val apiKey = dataStoreManager.geminiKeysFlow.first().firstOrNull() ?: ""
            if (apiKey.isBlank()) {
                return Result.failure(Exception("Gemini API Key সেট করা হয়নি। Menu -> API KEYS-এ যান।"))
            }

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
            )
            val response = geminiService.generateContent(apiKey = apiKey, request = request)
            
            val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response from Gemini."
            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
