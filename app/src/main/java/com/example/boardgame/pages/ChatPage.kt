package com.example.boardgame.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.boardgame.components.NewMessageDialog
import com.example.boardgame.data.ChatMessage
import com.example.boardgame.formatTimestamp
import com.example.boardgame.ui.theme.RobotoFontFamily

@Composable
fun ChatPage(
    eventId: String?,
    userId: String?,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var userName by remember { mutableStateOf("") }
    var isClicked by remember { mutableStateOf(false) }

    // Starte die Live-Aktualisierung der Nachrichten
    LaunchedEffect(eventId) {
        if (eventId != null) {
            dataViewModel.listenForMessages(eventId) { updatedMessages ->
                messages = updatedMessages
            }

            dataViewModel.fetchUserById(userId = userId, callback = { user ->
                userName = user?.firstName + " " + user?.lastName
            })
        }
    }
    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Chat",
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
                .background(colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Event Chat", style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(39.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    val messageTimestamp = formatTimestamp(message.timestamp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.dark_blue),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$userName:",
                                fontSize = 14.sp
                            )
                            Text(
                                text = messageTimestamp,
                                fontSize = 12.sp,
                                color = colorResource(R.color.light_grey)
                            )
                        }

                        Text(
                            text = message.message,
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                            fontSize = 18.sp
                        )
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.End) {
                FloatingActionButton(
                    onClick = { isClicked = !isClicked },
                    containerColor = colorResource(R.color.light_grey),
                    modifier = Modifier.size(76.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit button",
                        tint = colorResource(R.color.dark_blue),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            NewMessageDialog(
                showDialog = isClicked,
                onDismiss = { isClicked = !isClicked },
                eventId = eventId,
                userId = userId,
                dataViewModel = dataViewModel
            )
        }
    }
}
