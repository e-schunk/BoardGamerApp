package de.eduardschunk.boardplay.data

import androidx.annotation.Keep

@Keep
data class ChatMessage(
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)
