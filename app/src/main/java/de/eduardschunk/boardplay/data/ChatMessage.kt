package de.eduardschunk.boardplay.data

data class ChatMessage(
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)
