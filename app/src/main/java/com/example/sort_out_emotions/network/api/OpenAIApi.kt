package com.example.sort_out_emotions.network.api

import com.example.sort_out_emotions.data.model.ChatGPTResponse
import com.example.sort_out_emotions.utils.Constants
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApi {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${Constants.OPENAI_API_KEY}"
    )
    @POST("v1/completions")
    suspend fun getCompletion(
        @Body prompt: String
    ): ChatGPTResponse
}
