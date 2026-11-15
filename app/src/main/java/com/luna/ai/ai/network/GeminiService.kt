
package com.luna.ai.ai.network

import com.luna.ai.ai.model.GeminiRequest
import com.luna.ai.ai.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiService {

    @POST("v1beta/models/gemini-3.8-flash:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
