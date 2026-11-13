package com.luna.ai.ai.model

import com.google.gson.annotations.SerializedName

// Gemini API-এর জন্য রিকোয়েস্ট ফরম্যাট
data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>
)

data class GeminiContent(
    @SerializedName("parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text") val text: String
)

// Gemini API-এর জন্য রেসপন্স ফরম্যাট
data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent?
)
