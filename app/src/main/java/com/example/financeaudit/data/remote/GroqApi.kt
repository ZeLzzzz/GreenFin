package com.example.financeaudit.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApi {
    @POST("chat/completions")
    suspend fun analyzeImage(
        @Header("Authorization") apiKey: String,
        @Body request: GroqRequest
    ): GroqResponse
}