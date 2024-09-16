package com.example.sort_out_emotions.network.api

import com.example.sort_out_emotions.data.model.StableDiffusionResponse
import com.example.sort_out_emotions.utils.Constants
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface StableDiffusionApi {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${Constants.STABLE_DIFFUSION_API_KEY}"
    )
    @POST("generate")
    suspend fun generateImage(
        @Body prompt: String
    ): StableDiffusionResponse
}
