package de.eduardschunk.boardplay.data

data class Rating(
    val userId: String = "",
    val eventId: String = "",
    val title: String = "",
    val rating: Int = 0,
    val timestamp: Long = 0L
)
