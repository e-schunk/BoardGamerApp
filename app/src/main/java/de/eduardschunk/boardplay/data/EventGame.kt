package de.eduardschunk.boardplay.data

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class EventGame(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String = "",
    val gameId: String = "",
    val rating: Int = 0,
    val userLikes: Map<String, Boolean> = emptyMap(),
)
