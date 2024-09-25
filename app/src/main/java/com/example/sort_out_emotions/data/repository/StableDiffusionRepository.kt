// StableDiffusionRepository.kt
package com.example.sort_out_emotions.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.sort_out_emotions.data.model.GradioRequest
import com.example.sort_out_emotions.network.api.GradioApi
import com.example.sort_out_emotions.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

class StableDiffusionRepository {

    private val api: GradioApi

    init {
        val client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS) // 接続タイムアウトを120秒に設定
            .readTimeout(120, TimeUnit.SECONDS)    // 読み取りタイムアウトを120秒に設定
            .writeTimeout(120, TimeUnit.SECONDS)   // 書き込みタイムアウトを120秒に設定
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.GRADIO_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(GradioApi::class.java)
    }

    suspend fun generateImages(
        keywordss: String,
        features: List<Float>
    ): List<Bitmap> {
        // GradioRequestオブジェクトを作成
        val request = GradioRequest(
            data = listOf(
                keywordss, // キーワード
                features.getOrElse(0) { 0f }, // feature1
                features.getOrElse(1) { 0f }, // feature2
                features.getOrElse(2) { 0f }, // feature3
                features.getOrElse(3) { 0f }, // feature4
                features.getOrElse(4) { 0f }, // feature5
                features.getOrElse(5) { 0f }, // feature6
                features.getOrElse(6) { 0f }, // feature7
                features.getOrElse(7) { 0f }  // feature8
            )
        )

        // リクエストをログ出力
        Log.d("StableDiffusionRepository", "Gradioリクエスト: $request")

        try {
            val response = api.generateImage(request)

            // レスポンスから画像URLを抽出
            val imageUrls = response.data.flatten().mapNotNull { imageItem ->
                imageItem.image.url
            }

            // 画像をダウンロードしてBitmapに変換
            val bitmapList = imageUrls.mapNotNull { imageUrl ->
                downloadImage(imageUrl)
            }

            return bitmapList
        } catch (e: Exception) {
            Log.e("StableDiffusionRepository", "画像生成中にエラーが発生しました: ${e.message}")
            throw e
        }
    }

    private suspend fun downloadImage(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = URL(url).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e("StableDiffusionRepository", "画像のダウンロードに失敗しました: ${e.message}")
                null
            }
        }
    }
}
