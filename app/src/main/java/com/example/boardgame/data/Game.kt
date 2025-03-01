package com.example.boardgame.data

import com.example.boardgame.getCurrentDate
import java.util.UUID

data class Game(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getCurrentDate(),
    val image: String = ""
)
