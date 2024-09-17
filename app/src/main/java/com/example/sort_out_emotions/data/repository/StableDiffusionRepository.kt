package com.example.sort_out_emotions.data.repository

import com.example.sort_out_emotions.network.api.GradioApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.sort_out_emotions.utils.Constants
import java.util.concurrent.TimeUnit

class StableDiffusionRepository {

    private val api: GradioApi

    init {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // タイムアウトの設定
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.GRADIO_SERVER_URL) // GradioサーバーのURL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GradioApi::class.java)
    }

    suspend fun generateImages(summary: List<String>, sentiment: Map<String, Float>): ByteArray {
        val requestBody = mapOf(
            "summary" to summary,
            "sentiment" to sentiment
        )

        val response = api.generateImage(requestBody)
        return response.bytes()
    }
}
