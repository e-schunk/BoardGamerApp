package de.eduardschunk.boardplay.data

import androidx.annotation.Keep

@Keep
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
)
