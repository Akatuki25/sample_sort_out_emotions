// ChatGPTRepository.kt
package com.example.sort_out_emotions.data.repository

import android.util.Log
import com.example.sort_out_emotions.network.api.OpenAIApi
import com.example.sort_out_emotions.utils.Constants
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGPTRepository {

    private val api: OpenAIApi
    private var retryCount = 0

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.OPENAI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenAIApi::class.java)
    }

    // リクエスト用のデータクラスを定義
    data class ChatCompletionRequest(
        val model: String,
        val messages: List<Message>
    )

    data class Message(
        val role: String,
        val content: String
    )

    suspend fun summarizeText(text: String): String {
        val prompt = "以下の文章を要約して、重要な3つの英単語のキーワードをカンマ区切りの文字列のみで出力してください：\n$text"

        val request = ChatCompletionRequest(
            model = "gpt-4o",
            messages = listOf(
                Message(
                    role = "user",
                    content = prompt
                )
            )
        )

        val response = api.getCompletion(request)

        // レスポンスを取得
        val keywordsText = response.choices.firstOrNull()?.message?.content?.trim() ?: ""

        // ログに出力（デバッグ用）
        Log.d("ChatGPTRepository", "要約結果（キーワード）: $keywordsText")

        return keywordsText
    }

    suspend fun analyzeSentiment(text: String): Map<String, Float> {
        val prompt = """
            あなたは指定された文章の感情を数値化するアシスタントで、結果を以下のJSON形式のみで返します：
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
            以下の文章の感情を分析してください。
            文章：
            $text
        """.trimIndent()

        val request = ChatCompletionRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                Message(
                    role = "user",
                    content = prompt
                )
            )
        )

        val response = api.getCompletion(request)

        // レスポンスをパース
        val jsonText = response.choices.firstOrNull()?.message?.content ?: "{}"
        return parseJsonObject(jsonText, text)
    }

    private suspend fun parseJsonObject(jsonText: String, text: String): Map<String, Float> {
        // JSONオブジェクトをパースする処理
        return try {
            val jsonObject = JSONObject(jsonText)
            val keys = jsonObject.keys()
            val result = mutableMapOf<String, Float>()
            while (keys.hasNext()) {
                val key = keys.next()
                result[key] = jsonObject.getDouble(key).toFloat()
            }
            result
        } catch (e: Exception) {
            if(e is JsonSyntaxException || e is IllegalStateException){
                val maxRetries = 3
                if(retryCount < maxRetries){
                    Log.e("ChatGPTRepository", "JSONのパースに失敗しました。リトライします。")
                    retryCount++
                    return analyzeSentiment(text)
                } else {
                    Log.e("ChatGPTRepository", "JSONのパースに失敗しました。リトライ回数が上限に達しました。")
                    emptyMap()
                }
            } else {
                Log.e("ChatGPTRepository", "JSONのパースに失敗しました。")
                throw e
            }
        }
    }
}
