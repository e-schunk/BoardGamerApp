package com.example.boardgame.data

data class ChatMessage(
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)
