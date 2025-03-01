package com.example.boardgame.pages

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.boardgame.AuthState
import com.example.boardgame.AuthViewModel
import com.example.boardgame.DataViewModel
import com.example.boardgame.R
import com.example.boardgame.components.AppTopBar
import com.example.boardgame.data.Event
import com.example.boardgame.data.EventGame
import com.example.boardgame.data.Game
import com.example.boardgame.decodeBase64ToBitmap
import com.example.boardgame.ui.theme.RobotoFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun GameListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel,
    eventId: String?
) {
    val authState = authViewModel.authState.observeAsState()
    var gameList by remember { mutableStateOf<List<Game>>(emptyList()) }
    var event by remember { mutableStateOf<Event?>(null) }
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(eventId) {
        gameList = eventId?.let { dataViewModel.fetchGameListByEventId(it) }!!

        dataViewModel.fetchEventById(eventId) { loadedEvent ->
            event = loadedEvent
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Spiele",
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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Spiele", style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(39.dp)
                )
                IconButton(onClick = {
                    navController.navigate("gameSuggest/${event?.id}")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Add",
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            if (gameList.isEmpty()) {
                Spacer(modifier = Modifier.height(50.dp))
                Text( text = "Keine Spiele",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.dark_grey),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(28.dp))
            }
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(gameList) { game ->
                    var isLiked by remember { mutableStateOf(false) }
                    var isDisliked by remember { mutableStateOf(false) }
                    var eventGame by remember { mutableStateOf<EventGame?>(null) }
                    var rating by remember { mutableIntStateOf(eventGame?.rating ?: 0) }
                    val userId by remember { mutableStateOf(Firebase.auth.currentUser?.uid) }

                    LaunchedEffect(eventId, game.id) {
                        dataViewModel.fetchEventGameByEventId(eventId!!, game.id) {
                            eventGame = it
                            if (it != null) {
                                isLiked = it.userLikes[userId] == true
                                isDisliked = it.userLikes[userId] == false
                            }
                        }
                    }

                    // Neuladen, wenn sich 'rating' ändert
                    LaunchedEffect(rating) {
                        dataViewModel.fetchEventGameByEventId(eventId!!, game.id) {
                            eventGame = it
                        }
                    }

                    LaunchedEffect(eventGame) {
                        rating = eventGame?.rating ?: 0
                    }

                    ListItem(
                        headlineContent = { Text(game.title) },
                        supportingContent = { Text(game.description) },
                        leadingContent = {
                            Image(
                                bitmap = decodeBase64ToBitmap(game.image).asImageBitmap(),
                                contentDescription = "Game image",
                                modifier = Modifier.size(100.dp)
                            )
                        },
                        trailingContent = {
                            Column()
                            {
                                Row {
                                    // like Button
                                    IconButton(onClick = {
                                        rating += 1
                                        dataViewModel.saveUserLike(
                                            eventGame?.id,
                                            true,
                                            onSuccess = {
                                                eventGame?.let {
                                                    dataViewModel.updateRating(
                                                        it.id,
                                                        rating
                                                    )
                                                }
                                                isLiked = !isLiked
                                                isDisliked = false
                                            },
                                            onFailure = { e ->
                                                Toast.makeText(
                                                    context,
                                                    e.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })
                                    }) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                            contentDescription = "Like",
                                            Modifier.size(20.dp)
                                        )
                                    }
                                    // dislike Button
                                    IconButton(onClick = {
                                        rating = if (rating > 0) rating - 1 else 0
                                        dataViewModel.saveUserLike(
                                            eventGame?.id,
                                            false,
                                            onSuccess = {
                                                eventGame?.let {
                                                    dataViewModel.updateRating(
                                                        it.id,
                                                        rating
                                                    )
                                                }
                                                isDisliked = !isDisliked
                                                isLiked = false
                                            },
                                            onFailure = { e ->
                                                Toast.makeText(
                                                    context,
                                                    e.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })
                                    }) {
                                        Icon(
                                            imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                            contentDescription = "Disliked",
                                            Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = eventGame?.rating.toString(),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(R.color.black),
                                        fontFamily = RobotoFontFamily
                                    ),
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    )
                    HorizontalDivider(
                        color = colorResource(R.color.green),
                        thickness = 1.dp,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}