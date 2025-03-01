package com.example.boardgame.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.boardgame.AuthViewModel
import com.example.boardgame.DataViewModel
import com.example.boardgame.R
import com.example.boardgame.components.AppTopBar
import com.example.boardgame.components.StarRating
import com.example.boardgame.data.User
import com.example.boardgame.ui.theme.RobotoFontFamily

@Composable
fun RatingPage(
    eventId: String?,
    userId: String?,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {
    var user by remember { mutableStateOf(User()) }
    var eatRating by remember { mutableIntStateOf(0) }
    var gamesRating by remember { mutableIntStateOf(0) }
    var organizerRating by remember { mutableIntStateOf(0) }
    var generalRating by remember { mutableIntStateOf(0) }

    // Starte die Live-Aktualisierung der Nachrichten
    LaunchedEffect(eventId) {
        if (eventId != null) {
            dataViewModel.fetchUserById(userId = userId, callback = {
                if (it != null) {
                    user = it
                }
            })

            dataViewModel.fetchRatingForEventByUserId(
                eventId = eventId,
                userId = userId!!,
                title = "Gastgeber"
            ) { rating ->
                organizerRating = rating ?: 0
            }

            dataViewModel.fetchRatingForEventByUserId(
                eventId = eventId,
                userId = userId,
                title = "Essen"
            ) { rating ->
                eatRating = rating ?: 0
            }

            dataViewModel.fetchRatingForEventByUserId(
                eventId = eventId,
                userId = userId,
                title = "Spiele"
            ) { rating ->
                gamesRating = rating ?: 0
            }

            dataViewModel.fetchRatingForEventByUserId(
                eventId = eventId,
                userId = userId,
                title = "Allgemein"
            ) { rating ->
                generalRating = rating ?: 0
            }

        }
    }
    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Bewertung",
                onMenuClick = { navController.navigate("home") },
                dataViewModel = dataViewModel
            )
        },
        bottomBar = {
            TextButton(
                onClick = { authViewModel.singout() }
            ) {
                Text(text = "Ausloggen")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .background(colorResource(R.color.white)),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Bewerte das Event", style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black),
                    fontFamily = RobotoFontFamily
                ),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(39.dp)
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = "Gastgeber: " + user.firstName + " " + user.lastName,
                fontSize = 16.sp,
                color = colorResource(R.color.dark_blue),
                fontFamily = RobotoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(39.dp)

            )
            StarRating(
                rating = organizerRating,
                onRatingChanged = {
                    organizerRating = it
                    dataViewModel.updateEventRating(
                        userId = userId!!,
                        eventId = eventId!!,
                        title = "Gastgeber",
                        rating = it
                    )
                })
            Spacer(modifier = Modifier.size(40.dp))

            Text(
                text = "Essen",
                fontSize = 16.sp,
                color = colorResource(R.color.dark_blue),
                fontFamily = RobotoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(39.dp)
            )
            StarRating(
                rating = eatRating,
                onRatingChanged = {
                    eatRating = it
                    dataViewModel.updateEventRating(
                        userId = userId!!,
                        eventId = eventId!!,
                        title = "Essen",
                        rating = it
                    )
                })
            Spacer(modifier = Modifier.size(40.dp))

            Text(
                text = "Spiele",
                fontSize = 16.sp,
                color = colorResource(R.color.dark_blue),
                fontFamily = RobotoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(39.dp)
            )
            StarRating(
                rating = gamesRating,
                onRatingChanged = {
                    gamesRating = it
                    dataViewModel.updateEventRating(
                        userId = userId!!,
                        eventId = eventId!!,
                        title = "Spiele",
                        rating = it
                    )
                })
            Spacer(modifier = Modifier.size(40.dp))

            Text(
                text = "Allgemein",
                fontSize = 16.sp,
                color = colorResource(R.color.dark_blue),
                fontFamily = RobotoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(39.dp)
            )
            StarRating(
                rating = generalRating,
                onRatingChanged = {
                    generalRating = it
                    dataViewModel.updateEventRating(
                        userId = userId!!,
                        eventId = eventId!!,
                        title = "Allgemein",
                        rating = it
                    )
                })

        }
    }
}
