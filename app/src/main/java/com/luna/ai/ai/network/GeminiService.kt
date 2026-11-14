package com.luna.ai.ai.network

import com.luna.ai.ai.model.GeminiRequest
import com.luna.ai.ai.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiService {
    // ২০২৬ সালের সঠিক ও স্টেবল মডেল
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
