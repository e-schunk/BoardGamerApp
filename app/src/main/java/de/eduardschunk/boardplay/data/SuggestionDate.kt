package de.eduardschunk.boardplay.data

import android.os.Build
import androidx.annotation.RequiresApi
import de.eduardschunk.boardplay.pages.suggestions.ParticipantState
import java.util.UUID

data class SuggestionDate @RequiresApi(Build.VERSION_CODES.O) constructor(
    override val id: String = UUID.randomUUID().toString(),
    override val title: String = "",
    override val description: String = "",
    override val organizer: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val participants: Map<String, ParticipantState> = emptyMap(),
    val dates: Map<String, DateEntry> = emptyMap()
) : DataObject
