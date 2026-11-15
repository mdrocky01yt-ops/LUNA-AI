
package com.luna.ai.ai.repository

import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.model.GeminiContent
import com.luna.ai.ai.model.GeminiInlineData
import com.luna.ai.ai.model.GeminiPart
import com.luna.ai.ai.model.GeminiRequest
import com.luna.ai.ai.network.GeminiService
import com.luna.ai.core.datastore.DataStoreManager
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

@Singleton
class AiRepository @Inject constructor(
    private val geminiService: GeminiService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun sendMessage(
        history: List<ChatMessage>,
        imageBase64: String? = null
    ): Result<String> {

        try {

            // 1. API keys load
            val geminiKeys = dataStoreManager.geminiKeysFlow
                .first()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            if (geminiKeys.isEmpty()) {
                return Result.failure(
                    Exception(
                        "Gemini API Key পাওয়া যায়নি। API KEYS থেকে Key যোগ করুন।"
                    )
                )
            }

            // 2. System prompt আলাদা করা
            val systemPrompt = history.firstOrNull {
                it.role.equals("system", ignoreCase = true)
            }?.content?.trim()

            // 3. Conversation history তৈরি
            val conversation = history.filter {
                it.role.equals("user", ignoreCase = true) ||
                it.role.equals("assistant", ignoreCase = true) ||
                it.role.equals("model", ignoreCase = true)
            }

            if (conversation.isEmpty()) {
                return Result.failure(
                    Exception("কোনো User Message পাওয়া যায়নি।")
                )
            }

            // 4. Image validation
            val usableImage = when {

                imageBase64.isNullOrBlank() -> null

                imageBase64 == "Gemini" -> null

                imageBase64 == "gallery_selected" -> {
                    return Result.failure(
                        Exception(
                            "Gallery image এখনো সম্পূর্ণ প্রস্তুত নয়।"
                        )
                    )
                }

                else -> imageBase64.filterNot {
                    it.isWhitespace()
                }
            }

            // 5. সর্বশেষ User message খুঁজে বের করা
            val lastUserIndex = conversation.indexOfLast {
                it.role.equals("user", ignoreCase = true)
            }

            if (lastUserIndex == -1) {
                return Result.failure(
                    Exception("User Message পাওয়া যায়নি।")
                )
            }

            // 6. Gemini contents তৈরি
            val geminiContents = conversation.mapIndexed {
                index, message ->

                val role = when (message.role.lowercase()) {

                    "user" -> "user"

                    "assistant", "model" -> "model"

                    else -> "user"
                }

                val parts = mutableListOf<GeminiPart>()

                if (message.content.isNotBlank()) {
                    parts.add(
                        GeminiPart(text = message.content)
                    )
                }

                // শুধু সর্বশেষ User message-এর সঙ্গে image যোগ
                if (index == lastUserIndex && usableImage != null) {

                    parts.add(
                        GeminiPart(
                            inlineData = GeminiInlineData(
                                mimeType = "image/jpeg",
                                data = usableImage
                            )
                        )
                    )
                }

                GeminiContent(
                    role = role,
                    parts = parts
                )
            }

            // 7. System instruction তৈরি
            val systemInstruction = systemPrompt
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = it)
                        )
                    )
                }

            // 8. সম্পূর্ণ Gemini request
            val request = GeminiRequest(
                contents = geminiContents,
                systemInstruction = systemInstruction
            )

            // 9. API keys পরীক্ষা
            var lastError = "Gemini API request ব্যর্থ হয়েছে।"

            for (key in geminiKeys) {

                val response = try {

                    geminiService.generateContent(
                        apiKey = key,
                        request = request
                    )

                } catch (e: CancellationException) {

                    throw e

                } catch (e: IOException) {

                    return Result.failure(
                        Exception(
                            "Network connection failed: ${e.localizedMessage}"
                        )
                    )

                } catch (e: Exception) {

                    return Result.failure(
                        Exception(
                            "Request failed: ${e.localizedMessage}"
                        )
                    )
                }

                // 10. HTTP error handling
                if (!response.isSuccessful) {

                    val statusCode = response.code()

                    val errorBody = try {
                        response.errorBody()
                            ?.string()
                            ?.take(3000)
                            ?: response.message()
                    } catch (e: Exception) {
                        response.message()
                    }

                    lastError =
                        "Gemini HTTP Error $statusCode: $errorBody"

                    // নির্দিষ্ট error হলে পরের API key পরীক্ষা
                    val retryableCodes = setOf(
                        401, 403, 404, 429,
                        500, 502, 503, 504
                    )

                    if (statusCode !in retryableCodes) {
                        return Result.failure(
                            Exception(lastError)
                        )
                    }

                    continue
                }

                // 11. Successful response
                val responseBody = response.body()

                if (responseBody == null) {

                    lastError =
                        "Gemini successful response দিয়েছে, কিন্তু body খালি।"

                    continue
                }

                val reply = responseBody.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.mapNotNull { it.text }
                    ?.joinToString("\n")
                    ?.trim()

                if (!reply.isNullOrBlank()) {
                    return Result.success(reply)
                }

                lastError =
                    "Gemini কোনো text response দেয়নি। Safety filter অথবা empty response হতে পারে।"
            }

            return Result.failure(
                Exception(lastError)
            )

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            return Result.failure(
                Exception(
                    "Gemini Repository Error: ${e.localizedMessage}"
                )
            )
        }
    }
}
