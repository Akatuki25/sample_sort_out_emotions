package com.example.sort_out_emotions.data.repository

import android.util.Log
import com.example.sort_out_emotions.network.api.OpenAIApi
import com.example.sort_out_emotions.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGPTRepository {

    private val api: OpenAIApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.OPENAI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenAIApi::class.java)
    }

    suspend fun summarizeText(text: String): String {
        val prompt = "以下の文章を要約して、重要な3つの英単語のキーワードをカンマ区切りの文字列のみで返してください：\n$text"
        val response = api.getCompletion(prompt = mapOf("prompt" to prompt).toString())

        // レスポンスを取得
        val keywordsText = response.choices.firstOrNull()?.text?.trim() ?: ""

        // ログに出力（デバッグ用）
        Log.d("ChatGPTRepository", "要約結果（キーワード）: $keywordsText")

        return keywordsText
    }

    suspend fun analyzeSentiment(text: String): Map<String, Float> {
        val prompt = """
            以下の文章の感情を分析してください。結果を以下のJSON形式のみで返してください：
            {
                "joy": int,
                "sadness": int,
                "anticipation": int,
                "surprise": int,
                "anger": int,
                "fear": int,
                "disgust": int,
                "trust": int
            }
            なお、すべての数値は0から3の範囲で表し、2種類のみ1以上の数値を持たないようにしてください。 
            文章：
            $text
        """.trimIndent()

        val response = api.getCompletion(prompt = mapOf("prompt" to prompt).toString())

        // レスポンスをパース
        val jsonText = response.choices.firstOrNull()?.text ?: "{}"
        return parseJsonObject(jsonText)
    }

    private fun parseJsonObject(jsonText: String): Map<String, Float> {
        // JSONオブジェクトをパースする処理
        return try {
            val jsonObject = org.json.JSONObject(jsonText)
            val keys = jsonObject.keys()
            val result = mutableMapOf<String, Float>()
            while (keys.hasNext()) {
                val key = keys.next()
                result[key] = jsonObject.getDouble(key).toFloat()
            }
            result
        } catch (e: Exception) {
            Log.e("ChatGPTRepository", "感情分析結果のパースに失敗: ${e.message}")
            emptyMap()
        }
    }
}
