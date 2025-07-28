package de.eduardschunk.boardplay.data

import de.eduardschunk.boardplay.getCurrentDate
import java.util.UUID

data class Game(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getCurrentDate(),
    val image: String = ""
)
