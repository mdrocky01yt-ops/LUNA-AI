
package com.luna.ai.ai.model

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    @SerializedName("contents")
    val contents: List<GeminiContent>,

    @SerializedName("systemInstruction")
    val systemInstruction: GeminiContent? = null
)

data class GeminiContent(
    @SerializedName("role")
    val role: String? = null,

    @SerializedName("parts")
    val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("inlineData")
    val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    @SerializedName("mimeType")
    val mimeType: String,

    @SerializedName("data")
    val data: String
)

data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @SerializedName("content")
    val content: GeminiContent?
)
