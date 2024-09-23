package com.example.sort_out_emotions.data.repository

import com.example.sort_out_emotions.network.api.GradioApi
import com.example.sort_out_emotions.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    suspend fun generateImage(
        keyword: String,
        features: List<Float>
    ): ByteArray {
        // 固定のプロンプトを設定
        val prompts = listOf(
            "Impressionist",
            "Realist",
            "Surrealist",
            "Futurist",
            "Romanticist",
            "Dadaist",
            "Expressionist",
            "Cubist"
        )

        val requestBody = mutableMapOf<String, Any>(
            "keyword" to keyword
        )

        // prompt1〜prompt8を設定
        prompts.forEachIndexed { index, prompt ->
            requestBody["prompt${index + 1}"] = prompt
        }

        // feature1〜feature8を設定（featuresの各値）
        features.forEachIndexed { index, feature ->
            requestBody["feature${index + 1}"] = feature
        }

        val response = api.generateImage(requestBody)
        return response.bytes()
    }
}
