package com.luna.ai.ai.repository

import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.model.DeepSeekRequest
import com.luna.ai.ai.network.DeepSeekService
import com.luna.ai.core.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val apiService: DeepSeekService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun sendMessage(history: List<ChatMessage>): Result<String> {
        return try {
            val apiKey = dataStoreManager.apiKeyFlow.first()
            if (apiKey.isBlank()) {
                return Result.failure(Exception("API Key সেট করা হয়নি। দয়া করে Settings-এ গিয়ে Key বসান।"))
            }

            val request = DeepSeekRequest(messages = history)
            val response = apiService.sendMessage(
                auth = "Bearer $apiKey",
                request = request
            )
            
            val reply = response.choices.firstOrNull()?.message?.content ?: "কোনো উত্তর পাওয়া যায়নি।"
            Result.success(reply)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
