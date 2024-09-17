package com.example.sort_out_emotions.network.api

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GradioApi {

    @Headers("Content-Type: application/json")
    @POST("/predict")
    suspend fun generateImage(
        @Body requestBody: Map<String, Any>
    ): ResponseBody
}
