// GradioApi.kt
package com.example.sort_out_emotions.network.api

import com.example.sort_out_emotions.data.model.GradioRequest
import com.example.sort_out_emotions.data.model.StableDiffusionResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// GradioApi.kt
interface GradioApi {
    @Headers("Content-Type: application/json")
    @POST("/run/predict") // エンドポイントを修正
    suspend fun generateImage(@Body request: GradioRequest): StableDiffusionResponse
}

