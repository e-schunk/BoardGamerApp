package de.eduardschunk.boardplay.data

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class DateEntry(
    val key: String = UUID.randomUUID().toString(),
    val date: String = "",
    val counter: Int = 0,
    val participants: List<String> = emptyList()
)
