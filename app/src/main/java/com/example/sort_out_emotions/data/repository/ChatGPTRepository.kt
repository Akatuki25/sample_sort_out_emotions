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

    suspend fun summarizeText(text: String): List<String> {
        val prompt = "以下の文章を要約して、重要な3つの単語をリスト形式で返してください：\n$text\n返答はJSON配列のみで返してください。"
        val response = api.getCompletion(prompt = mapOf("prompt" to prompt).toString())

        // レスポンスをパース
        val jsonText = response.choices.firstOrNull()?.text ?: "[]"
        return parseJsonArray(jsonText)
    }

    suspend fun analyzeSentiment(text: String): Map<String, Float> {
        val prompt = """
            以下の文章の感情を分析してください。結果を以下のJSON形式で返してください：
            {
                "joy": int,
                "sadness": int,
                "anticipation": int,
                "surprise": int,
                "anger": int,
                "fear": int,
                "disgust": int,
                "trust": int,
                "sentiment": int
            }
            条件:sentiment-2~2の範囲で、文章の感情を-2（非常にネガティブ）から2（非常にポジティブ）までの範囲で評価してください。それ以外の指標は0~3の範囲で評価してください。
            文章：
            $text
        """.trimIndent()

        val response = api.getCompletion(prompt = mapOf("prompt" to prompt).toString())

        // レスポンスをパース
        val jsonText = response.choices.firstOrNull()?.text ?: "{}"
        return parseJsonObject(jsonText)
    }

    private fun parseJsonArray(jsonText: String): List<String> {
        // JSON配列をパースする処理
        return try {
            val jsonArray = org.json.JSONArray(jsonText)
            List(jsonArray.length()) { i -> jsonArray.getString(i) }
        } catch (e: Exception) {
            Log.e("ChatGPTRepository", "要約結果のパースに失敗: ${e.message}")
            emptyList()
        }
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
