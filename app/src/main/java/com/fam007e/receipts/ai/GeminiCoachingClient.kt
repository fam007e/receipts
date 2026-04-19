package com.fam007e.receipts.ai

import com.fam007e.receipts.data.preferences.UserPreferences
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

class GeminiCoachingClient @Inject constructor(
    private val httpClient: HttpClient,
    private val userPreferences: UserPreferences
) {
    suspend fun getAdvice(categoryName: String, totalCount: Int, note: String): String {
        val apiKey = userPreferences.geminiApiKey.first() ?: com.fam007e.receipts.BuildConfig.GEMINI_API_KEY
        val baseUrl = userPreferences.aiBaseUrl.first()
        
        val prompt = """
            You are a calm, empathetic relationship coach. 
            A user noticed something that bothered them: "$categoryName".
            This is the $totalCount time they've noticed this.
            ${if (note.isNotEmpty()) "Their note was: $note" else ""}
            
            How should they bring this up without it turning into an argument?
            Suggest specific, non-accusatory words and what to expect in response.
            Keep it warm and practical.
        """.trimIndent()

        return try {
            val response = httpClient.post(baseUrl + "chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(OpenAIRequest(
                    model = "gemini-1.5-flash", // Or configurable
                    messages = listOf(
                        Message("user", prompt)
                    )
                ))
            }
            
            if (response.status.isSuccess()) {
                val body = Json.decodeFromString<OpenAIResponse>(response.bodyAsText())
                body.choices.firstOrNull()?.message?.content ?: "No response from coach."
            } else {
                "Coach is currently unavailable (Status: ${response.status})"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Failed to connect to the coach: ${e.message}"
        }
    }
}
