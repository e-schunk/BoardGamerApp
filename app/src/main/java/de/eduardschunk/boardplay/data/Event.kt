package de.eduardschunk.boardplay.data

import de.eduardschunk.boardplay.pages.events.UserState
import java.util.UUID

data class Event(
    override val id: String = UUID.randomUUID().toString(),
    override val title: String = "",
    override val description: String = "",
    override val organizer: String = "",
    val date: String = "",
    val time: String = "",
    val participants: Map<String, UserState> = emptyMap(),
    val games: List<EventGame> = emptyList(),
    val location: String = ""
) : DataObject
