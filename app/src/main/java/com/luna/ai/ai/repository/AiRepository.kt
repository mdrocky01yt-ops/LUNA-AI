package com.luna.ai.ai.repository

import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.model.GeminiContent
import com.luna.ai.ai.model.GeminiInlineData
import com.luna.ai.ai.model.GeminiPart
import com.luna.ai.ai.model.GeminiRequest
import com.luna.ai.ai.network.GeminiService
import com.luna.ai.core.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val geminiService: GeminiService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun sendMessage(
        history: List<ChatMessage>, 
        provider: String, // এটি এখন আর ব্যবহার হবে না, কিন্তু ভিউমডেল এরর এড়াতে রাখা হলো
        imageBase64: String? = null 
    ): Result<String> {
        
        val geminiKeys = dataStoreManager.geminiKeysFlow.first()
        if (geminiKeys.isEmpty()) {
            return Result.failure(Exception("Gemini API Key সেট করা হয়নি। API KEYS-এ গিয়ে Key বসান।"))
        }

        val lastUserMessage = history.lastOrNull { it.role == "user" }?.content ?: ""
        
        for (key in geminiKeys) {
            try {
                val parts = mutableListOf<GeminiPart>()
                parts.add(GeminiPart(text = lastUserMessage))
                if (imageBase64 != null) {
                    parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = imageBase64)))
                }

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = parts))
                )
                
                // গুগলের সার্ভারে রিকোয়েস্ট পাঠানো হচ্ছে
                val response = geminiService.generateContent(apiKey = key, request = request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (!reply.isNullOrBlank()) {
                    return Result.success(reply)
                }
            } catch (e: Exception) { 
                // আসল এররটা লগে দেখা যাবে, কিন্তু ইউজারকে জানানোর জন্য নিচে পাঠানো হবে
                return Result.failure(Exception("Network Error: ${e.message}"))
            }
        }
        
        return Result.failure(Exception("Gemini API Key সঠিক নয়।"))
    }
}
