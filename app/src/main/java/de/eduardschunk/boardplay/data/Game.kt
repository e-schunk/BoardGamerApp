package de.eduardschunk.boardplay.data

import androidx.annotation.Keep
import de.eduardschunk.boardplay.getCurrentDate
import java.util.UUID

@Keep
data class Game(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = getCurrentDate(),
    val image: String = ""
)
