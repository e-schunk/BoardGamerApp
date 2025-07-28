package de.eduardschunk.boardplay

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.eduardschunk.boardplay.pages.DashboardPage
import de.eduardschunk.boardplay.pages.LoginPage
import de.eduardschunk.boardplay.pages.MainPage
import de.eduardschunk.boardplay.pages.SignupPage
import de.eduardschunk.boardplay.pages.events.ChatPage
import de.eduardschunk.boardplay.pages.events.EventDetailPage
import de.eduardschunk.boardplay.pages.events.EventListPage
import de.eduardschunk.boardplay.pages.events.GameListPage
import de.eduardschunk.boardplay.pages.events.GameSuggestPage
import de.eduardschunk.boardplay.pages.events.GamerListPage
import de.eduardschunk.boardplay.pages.events.GamerSuggestPage
import de.eduardschunk.boardplay.pages.events.NewEventPage
import de.eduardschunk.boardplay.pages.events.NewGamePage
import de.eduardschunk.boardplay.pages.events.RatingPage
import de.eduardschunk.boardplay.pages.events.RatingSummeryPage
import de.eduardschunk.boardplay.pages.invitation.InvitationListPage
import de.eduardschunk.boardplay.pages.suggestions.NewSuggestionDatePage
import de.eduardschunk.boardplay.pages.suggestions.ParticipantListPage
import de.eduardschunk.boardplay.pages.suggestions.ParticipantSuggestPage
import de.eduardschunk.boardplay.pages.suggestions.SuggestionDateDetailPage
import de.eduardschunk.boardplay.pages.suggestions.SuggestionDateListPage
import de.eduardschunk.boardplay.pages.suggestions.SuggestionDatesEditPage
import de.eduardschunk.boardplay.pages.suggestions.SuggestionDatesViewPage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, dataViewModel: DataViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home", builder = {
        composable("login"){
            LoginPage(modifier, navController, authViewModel, dataViewModel)
        }
        composable("signup"){
            SignupPage(modifier, navController, authViewModel, dataViewModel)
        }
        composable("home"){
            MainPage(modifier, navController, authViewModel)
        }
        //Event
        composable("eventList"){
            EventListPage(modifier, navController, authViewModel, dataViewModel)
        }
        composable("newEvent"){
            NewEventPage(modifier, navController, authViewModel, dataViewModel)
        }
        composable("eventDetail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            EventDetailPage(modifier, navController, authViewModel, dataViewModel, eventId)
        }
        composable("dashboard"){
            DashboardPage(modifier, navController, authViewModel, dataViewModel)
        }
        //Game
        composable("gameList/{eventId}"){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            GameListPage(modifier, navController, authViewModel, dataViewModel, eventId)
        }
        composable("gameSuggest/{eventId}"){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            GameSuggestPage(modifier, navController, authViewModel, dataViewModel, eventId)
        }
        composable("newGame/{eventId}"){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            NewGamePage(modifier, navController, authViewModel, dataViewModel, eventId)
        }
        //Gamer
        composable("gamerList/{eventId}"){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            GamerListPage(modifier, navController, authViewModel, dataViewModel, eventId)
        }
        composable("gamerSuggest/{eventId}"){ backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            GamerSuggestPage(modifier, navController, authViewModel, dataViewModel, eventId)
        }
        //Invitation
        composable("invitation"){
            InvitationListPage(modifier, navController, authViewModel, dataViewModel)
        }
        //Chat
        composable("chat/{eventId}/{userId}"){
            backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val userId = backStackEntry.arguments?.getString("userId")
            ChatPage(eventId, userId, navController, authViewModel, dataViewModel)
        }
        //Bewertung
        composable("rating/{eventId}/{userId}"){
            backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val userId = backStackEntry.arguments?.getString("userId")
            RatingPage(eventId, userId, navController, authViewModel, dataViewModel)
        }
        composable("ratingSummery/{eventId}"){
            backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            RatingSummeryPage(eventId, navController, authViewModel, dataViewModel)
        }
        //Terminvorschläge
        composable("suggestDateList"){
            SuggestionDateListPage(modifier, navController, authViewModel, dataViewModel)
        }
        composable("newSuggestionDate"){
            NewSuggestionDatePage(modifier, navController, authViewModel, dataViewModel)
        }
        composable("suggestionDateDetail/{suggestionDateId}") { backStackEntry ->
            val suggestionDateId = backStackEntry.arguments?.getString("suggestionDateId")
            if (suggestionDateId != null) {
                SuggestionDateDetailPage(modifier, navController, authViewModel, dataViewModel, suggestionDateId)
            }
        }
        composable("participantList/{suggestionDateId}") { backStackEntry ->
            val suggestionDateId = backStackEntry.arguments?.getString("suggestionDateId")
            if (suggestionDateId != null) {
                ParticipantListPage(modifier, navController, authViewModel, dataViewModel, suggestionDateId)
            }
        }
        composable("participantSuggest/{suggestionDateId}") { backStackEntry ->
            val suggestionDateId = backStackEntry.arguments?.getString("suggestionDateId")
            if (suggestionDateId != null) {
                ParticipantSuggestPage(modifier, navController, authViewModel, dataViewModel, suggestionDateId)
            }
        }
        composable("suggestionEditDates/{suggestionDateId}") { backStackEntry ->
            val suggestionDateId = backStackEntry.arguments?.getString("suggestionDateId")
            if (suggestionDateId != null) {
                SuggestionDatesEditPage(modifier, navController, authViewModel, dataViewModel, suggestionDateId)
            }
        }
        composable("suggestionViewDates/{suggestionDateId}") { backStackEntry ->
            val suggestionDateId = backStackEntry.arguments?.getString("suggestionDateId")
            if (suggestionDateId != null) {
                SuggestionDatesViewPage(modifier, navController, authViewModel, dataViewModel, suggestionDateId)
            }
        }
    })
}