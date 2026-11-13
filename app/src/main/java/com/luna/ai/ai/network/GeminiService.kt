package com.luna.ai.ai.network

import com.luna.ai.ai.model.GeminiRequest
import com.luna.ai.ai.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiService {
    // Gemini API-তে Key কুয়েরি প্যারামিটার হিসেবে যায়
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
