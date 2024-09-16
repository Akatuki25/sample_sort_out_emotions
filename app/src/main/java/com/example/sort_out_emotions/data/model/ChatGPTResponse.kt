package com.example.sort_out_emotions.data.model

data class ChatGPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val text: String
)
