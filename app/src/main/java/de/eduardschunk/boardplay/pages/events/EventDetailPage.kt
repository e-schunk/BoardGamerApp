package de.eduardschunk.boardplay.pages.events

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import de.eduardschunk.boardplay.AuthState
import de.eduardschunk.boardplay.AuthViewModel
import de.eduardschunk.boardplay.DataViewModel
import de.eduardschunk.boardplay.R
import de.eduardschunk.boardplay.components.AppTopBar
import de.eduardschunk.boardplay.data.Event
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailPage(
    modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, dataViewModel: DataViewModel, eventId: String?
) {
    val authState = authViewModel.authState.observeAsState()
    var event by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(eventId) {
        dataViewModel.fetchEventById(eventId) { loadedEvent ->
            event = loadedEvent
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            titel = "Event Details", onMenuClick = { navController.navigate("home") }, dataViewModel = dataViewModel
        )
    }, bottomBar = {
        TextButton(
            onClick = { authViewModel.singout() }) {
            Text(text = "Ausloggen")
        }
    }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.white)), horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = event?.title ?: "Titel fehlt", style = TextStyle(
                    fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.black), fontFamily = RobotoFontFamily
                ), modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(39.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Am " + event?.date, style = TextStyle(
                    fontSize = 14.sp, fontWeight = FontWeight.Normal, color = colorResource(R.color.dark_blue), fontFamily = RobotoFontFamily
                ), modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(18.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            event?.description?.let {
                Text(
                    text = it, style = TextStyle(
                        fontSize = 16.sp, fontWeight = FontWeight.Normal, color = colorResource(R.color.black), fontFamily = RobotoFontFamily
                    ), modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .padding(top = 80.dp, bottom = 20.dp)
            ) {
                Column {
                    LargeFloatingActionButton(
                        onClick = {
                            navController.navigate("gamerList/$eventId")
                        }, containerColor = colorResource(R.color.dark_blue)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = "Gamer button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Spieler", style = TextStyle(
                            fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = RobotoFontFamily, color = colorResource(R.color.black)
                        ), modifier = Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Column {
                    LargeFloatingActionButton(
                        onClick = {
                            navController.navigate("gameList/$eventId")
                        }, containerColor = colorResource(R.color.dark_blue)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mood,
                            contentDescription = "Games button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Spiele", style = TextStyle(
                            fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = RobotoFontFamily, color = colorResource(R.color.black)
                        ), modifier = Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 50.dp)
            ) {

                Column {
                    LargeFloatingActionButton(
                        onClick = {
                            navController.navigate("chat/$eventId/${Firebase.auth.currentUser?.uid}")
                        }, containerColor = colorResource(R.color.dark_blue)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChatBubble,
                            contentDescription = "Rating button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Chat", style = TextStyle(
                            fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = RobotoFontFamily, color = colorResource(R.color.black)
                        ), modifier = Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                if (isEventOver(event)) {
                    Column {
                        LargeFloatingActionButton(
                            onClick = {
                                if (Firebase.auth.currentUser?.uid == event?.organizer) {
                                    navController.navigate("ratingSummery/$eventId")
                                } else {
                                    navController.navigate("rating/$eventId/${Firebase.auth.currentUser?.uid}")
                                }
                            }, containerColor = colorResource(R.color.dark_blue)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating button",
                                tint = colorResource(R.color.white),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            if (Firebase.auth.currentUser?.uid == event?.organizer) {
                                "Bewertungen"
                            } else {
                                "Bewerten"
                            }, style = TextStyle(
                                fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = RobotoFontFamily, color = colorResource(R.color.black)
                            ), modifier = Modifier
                                .padding(top = 10.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isEventOver(event: Event?): Boolean {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val currentDate = java.time.LocalDate.now()
    val eventDate = event?.date?.let { java.time.LocalDate.parse(it, formatter) }
    return eventDate != null && eventDate < currentDate
}
