package com.example.boardgame

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.boardgame.pages.ChatPage
import com.example.boardgame.pages.DashboardPage
import com.example.boardgame.pages.EventDetailPage
import com.example.boardgame.pages.LoginPage
import com.example.boardgame.pages.MainPage
import com.example.boardgame.pages.NewEventPage
import com.example.boardgame.pages.SignupPage
import com.example.boardgame.pages.EventListPage
import com.example.boardgame.pages.GameListPage
import com.example.boardgame.pages.GameSuggestPage
import com.example.boardgame.pages.GamerListPage
import com.example.boardgame.pages.GamerSuggestPage
import com.example.boardgame.pages.InvitationListPage
import com.example.boardgame.pages.NewGamePage
import com.example.boardgame.pages.RatingPage
import com.example.boardgame.pages.RatingSummeryPage

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
    })
}