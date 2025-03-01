package com.example.boardgame.data

import com.example.boardgame.pages.UserState
import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val description: String = "",
    val organizer: String = "",
    val participants: Map<String, UserState> = emptyMap(),
    val games: List<EventGame> = emptyList()
)
