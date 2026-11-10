package com.luna.ai.ai.network

import com.luna.ai.ai.model.DeepSeekRequest
import com.luna.ai.ai.model.DeepSeekResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService {
    @POST("chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") auth: String,
        @Body request: DeepSeekRequest
    ): DeepSeekResponse
}
