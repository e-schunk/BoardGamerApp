package com.example.boardgame

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.boardgame.data.ChatMessage
import com.example.boardgame.data.Event
import com.example.boardgame.data.EventGame
import com.example.boardgame.data.Game
import com.example.boardgame.data.Rating
import com.example.boardgame.data.User
import com.example.boardgame.pages.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DataViewModel : ViewModel() {
    private val db = Firebase.database

    fun saveEvent(event: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val myRef = db.getReference("events")

        myRef.child(event.id).setValue(event)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchEvents(callback: (List<Event>) -> Unit) {
        val myRef = db.getReference("events")
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val today = LocalDate.now()
                val oneWeekAgo = today.minusWeeks(1) // Grenze für Events setzen

                val eventList = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                    .filter { event ->
                        try {
                            val eventDate = LocalDate.parse(event.date, dateFormatter)
                            eventDate.isAfter(oneWeekAgo) || eventDate.isEqual(oneWeekAgo)
                        } catch (e: Exception) {
                            false
                        }
                    }

                callback(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Fehler beim Abrufen der Daten: ${error.message}")
            }
        })
    }

    fun fetchEventById(eventId: String?, callback: (Event?) -> Unit) {
        val myRef = db.getReference("events")
        myRef.get().addOnSuccessListener { snapshot ->
            val event = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                .find { it.id == eventId }
            callback(event)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun saveGame(game: Game, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val myRef = db.getReference("games")

        myRef.child(game.id).setValue(game)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    suspend fun fetchGameListByEventId(eventId: String): List<Game> = coroutineScope {
        val eventGamesSnapshot =
            db.getReference("eventGames").orderByChild("eventId").equalTo(eventId).get().await()
        val gameIds = eventGamesSnapshot.children.mapNotNull {
            it.child("gameId").getValue(String::class.java)
        }

        val deferredGames = gameIds.map { gameId ->
            async { fetchGameById(gameId) }
        }

        deferredGames.mapNotNull { it.await() }
    }

    private suspend fun fetchGameById(gameId: String): Game? {
        val snapshot = db.getReference("games").child(gameId).get().await()
        return snapshot.getValue(Game::class.java)
    }

    fun saveEventGame(eventGame: EventGame, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val myRef = db.getReference("eventGames")
        myRef.child(eventGame.id).setValue(eventGame)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun updateRating(eventGameId: String, rating: Int) {
        val myRef = db.getReference("eventGames")
        myRef.child(eventGameId).child("rating").setValue(rating)
    }

    fun searchInGames(query: String, callback: (List<Game>) -> Unit) {
        val myRef = db.getReference("games")
        myRef.orderByChild("title")
            .startAt(query)
            .get()
            .addOnSuccessListener { snapshot ->
                val gameList = snapshot.children.mapNotNull { it.getValue(Game::class.java) }
                callback(gameList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun fetchEventGameByEventId(eventId: String, gameId: String, callback: (EventGame?) -> Unit) {
        val myRef = db.getReference("eventGames")
        myRef.get().addOnSuccessListener { snapshot ->
            val eventGame = snapshot.children.mapNotNull { it.getValue(EventGame::class.java) }
                .find { it.eventId == eventId && it.gameId == gameId }
            callback(eventGame)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun saveUserLike(
        eventGameId: String?,
        liked: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = Firebase.auth.currentUser
        val myRef = db.getReference("eventGames")
        if (eventGameId != null && user != null) {
            myRef.child(eventGameId).child("userLikes").child(user.uid).setValue(liked)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }
    }

    fun saveUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val myRef = db.getReference("users")
        myRef.child(user.id).setValue(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun fetchUserById(userId: String?, callback: (User?) -> Unit) {
        if (userId.isNullOrEmpty()) {
            callback(null) // Falls userId null oder leer ist, direkt abbrechen
            return
        }

        val myRef = db.getReference("users").child(userId)

        myRef.get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    suspend fun fetchUserListByEventId(eventId: String): List<User> = coroutineScope {
        val eventSnapshot = db.getReference("events").child(eventId).get().await()
        val participantsSnapshot = eventSnapshot.child("participants").children

        val deferredUsers = participantsSnapshot.mapNotNull { participantSnapshot ->
            participantSnapshot.key?.let { userId ->
                async { fetchUserById(userId) }
            }
        }
        deferredUsers.awaitAll().mapNotNull { it }
    }

    private suspend fun fetchUserById(userId: String): User? {
        val snapshot = db.getReference("users").child(userId).get().await()
        return snapshot.getValue(User::class.java)
    }

    fun checkUserState(user: User, event: Event?): UserState? {
        if (event != null) {
            event.participants[user.id]?.let {
                return it
            }
        }
        return null
    }

    fun getOrganizerName(event: Event, callback: (String?) -> Unit) {
        val userId = event.organizer
        fetchUserById(userId, callback = { user ->
            val userName = (user?.firstName ?: "Anonymer Benutzer") + " " + (user?.lastName ?: "")
            callback(userName)
        })
    }

    fun searchInUsers(query: String, callback: (List<User>) -> Unit) {
        val myRef = db.getReference("users")
        myRef.orderByChild("firstName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val gamerList = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                callback(gamerList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun addGamerToEvent(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val myRef = db.getReference("events")
        myRef.child(eventId).child("participants").child(userId)
            .setValue(UserState.PendingInvitation)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    suspend fun fetchEventsByUserId(userId: String): List<Event> {
        return withContext(Dispatchers.IO) {
            val eventSnapshot = db.getReference("events").get().await()

            eventSnapshot.children.mapNotNull { event ->
                val participantStatus =
                    event.child("participants").child(userId).getValue(UserState::class.java)

                if (participantStatus == UserState.PendingInvitation) {
                    event.getValue(Event::class.java)
                } else {
                    null
                }
            }
        }
    }

    fun acceptInvitation(event: Event) {
        val myRef = db.getReference("events").child(event.id)
        myRef.child("participants").child(Firebase.auth.currentUser!!.uid)
            .setValue(UserState.AcceptInvitation)
    }

    fun declineInvitation(event: Event) {
        val myRef = db.getReference("events").child(event.id)
        myRef.child("participants").child(Firebase.auth.currentUser!!.uid)
            .setValue(UserState.DeclinedInvitation)
    }

    // Chat
    fun sendMessage(eventId: String, userId: String, message: String) {
        val db = db.getReference("events/$eventId/messages")
        val messageId = db.push().key // Automatische ID generieren

        if (messageId != null) {
            val messageData = mapOf(
                "userId" to userId,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )

            db.child(messageId).setValue(messageData)
        }
    }

    fun listenForMessages(eventId: String, onMessagesUpdated: (List<ChatMessage>) -> Unit) {
        val db = db.getReference("events/$eventId/messages")

        db.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                onMessagesUpdated(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Chat konnte nicht geladen werden: ${error.message}")
            }
        })
    }

    fun updateEventRating(userId: String, eventId: String, title: String, rating: Int) {
        val db = db.getReference("events/$eventId/ratings")
        db.orderByChild("userId").equalTo(userId).get()
            .addOnSuccessListener { snapshot ->
                var existingRatingId: String? = null

                for (child in snapshot.children) {
                    val ratingData = child.value as? Map<*, *>
                    val existingTitle = ratingData?.get("title") as? String

                    if (existingTitle == title) {
                        existingRatingId = child.key // Speichert die vorhandene ID
                        break
                    }
                }

                val ratingData = mapOf(
                    "userId" to userId,
                    "title" to title,
                    "rating" to rating,
                    "timestamp" to System.currentTimeMillis()
                )

                if (existingRatingId != null) {
                    db.child(existingRatingId).setValue(ratingData)
                } else {
                    val newRatingId = db.push().key
                    if (newRatingId != null) {
                        db.child(newRatingId).setValue(ratingData)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Fehler beim Abrufen der Bewertungen: ${it.message}")
            }
    }

    fun fetchRatingForEventByUserId(
        eventId: String,
        userId: String,
        title: String,
        callback: (Int?) -> Unit
    ) {
        val myRef = db.getReference("events/$eventId/ratings/")
            .orderByChild("userId")
            .equalTo(userId)

        myRef.get()
            .addOnSuccessListener { snapshot ->
                val rating = snapshot.children
                    .mapNotNull { it.getValue(Rating::class.java) }
                    .find { it.title == title }
                callback(rating?.rating)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Fehler beim Abrufen der Bewertungen: ${it.message}")
            }
    }

    fun fetchRatingSummeryForEventByTitle(eventId: String, title: String, callback: (Double?) -> Unit) {
        val myRef = db.getReference("events").child(eventId).child("ratings")
        myRef.orderByChild("title").equalTo(title).get().addOnSuccessListener { snapshot ->
            val ratings = snapshot.children.mapNotNull { it.getValue(Rating::class.java) }
            val averageRating = ratings.map { it.rating }.average()
            callback(averageRating)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun deleteEvent(event: Event) {
        val myRef = db.getReference("events").child(event.id)
        myRef.removeValue()
    }

}