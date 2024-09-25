// ChatGPTResponse.kt
package com.example.sort_out_emotions.data.model

data class ChatGPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: ResponseMessage,
    val finish_reason: String
)

data class ResponseMessage(
    val role: String,
    val content: String
)
