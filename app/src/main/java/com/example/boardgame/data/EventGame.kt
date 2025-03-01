package com.example.boardgame.data

import java.util.UUID

data class EventGame(
    val id: String = UUID.randomUUID().toString(),
    val eventId: String = "",
    val gameId: String = "",
    val rating: Int = 0,
    val userLikes: Map<String, Boolean> = emptyMap(),
)
