package com.example.financeaudit.data.remote

import com.google.gson.annotations.SerializedName

data class GroqRequest(
    val model: String = "meta-llama/llama-4-maverick-17b-128e-instruct",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.0,
    @SerializedName("response_format") val responseFormat: ResponseFormat = ResponseFormat(type = "json_object")
)

data class ResponseFormat(val type: String)

data class GroqMessage(
    val role: String = "user",
    val content: List<ContentPart>
)

sealed class ContentPart {
    data class Text(
        val type: String = "text",
        val text: String
    ) : ContentPart()

    data class Image(
        val type: String = "image_url",
        val image_url: ImageUrl
    ) : ContentPart()
}

data class ImageUrl(val url: String)

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessageContent
)

data class GroqMessageContent(
    val content: String
)

data class ExtractedTransaction(
    val type: String,
    val amount: Long,
    val category: String,
    val wallet_name: String,
    val note: String
)