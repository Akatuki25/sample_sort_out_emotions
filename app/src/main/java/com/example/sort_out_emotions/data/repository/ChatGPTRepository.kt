package com.example.sort_out_emotions.data.repository

import com.example.sort_out_emotions.network.api.OpenAIApi
import com.example.sort_out_emotions.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class ChatGPTRepository {

    private val api: OpenAIApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.OPENAI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenAIApi::class.java)
    }

    suspend fun summarizeText(text: String): List<String> {
        val prompt = "以下の文章を要約して3つのキーワードを返してください：\n$text"
        val response = api.getCompletion(prompt = prompt)
        // レスポンスをログに出力
        Log.d("ChatGPTRepository", "要約APIのレスポンス: ${response.choices.firstOrNull()?.text}")

        // レスポンスのパース（例としてカンマ区切りとします）
        return response.choices.firstOrNull()?.text?.split(",") ?: emptyList()
    }

    suspend fun analyzeSentiment(text: String): Float {
        val prompt = "以下の文章をjoy,sadness,anticipation,surprise,anger,fear,disgust,trust,sentimentの感情の数値のみで評価してください。評価条件は、sentimentは-2,-1,0,1,2で、それ以外は0,1,2,3で表す：\n$text"
        val response = api.getCompletion(prompt = prompt)
        // レスポンスをログに出力
        Log.d("ChatGPTRepository", "感情分析APIのレスポンス: ${response.choices.firstOrNull()?.text}")

        val sentimentText = response.choices.firstOrNull()?.text ?: "0.5"
        return sentimentText.toFloatOrNull() ?: 0.5f
    }
}
