package com.fam007e.receipts.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject

class GeminiCoachingClient @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    suspend fun getAdvice(categoryName: String, totalCount: Int, note: String): String {
        val prompt = """
            You are a calm, empathetic relationship coach. 
            A user noticed something that bothered them: "$categoryName".
            This is the $totalCount time they've noticed this.
            ${if (note.isNotEmpty()) "Their note was: $note" else ""}
            
            How should they bring this up without it turning into an argument?
            Suggest specific, non-accusatory words and what to expect in response.
            Keep it warm and practical.
        """.trimIndent()

        val response = generativeModel.generateContent(
            content {
                text(prompt)
            }
        )
        return response.text ?: "I couldn't generate advice at this time."
    }
}
