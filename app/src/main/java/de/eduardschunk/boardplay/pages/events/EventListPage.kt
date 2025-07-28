package de.eduardschunk.boardplay.pages.events

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    var eventList by remember { mutableStateOf<List<Event>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(showDialog) {
        dataViewModel.fetchEvents { events ->
            eventList = events
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Termine",
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
                    "Termine", style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(39.dp)
                )
                IconButton(onClick = {
                    navController.navigate("newEvent")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Add",
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            if (eventList.isEmpty()) {
                Spacer(modifier = Modifier.height(50.dp))
                Text( text = "Du hast keine Events",
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
                items(eventList) { event ->
                    var organizerName by remember { mutableStateOf("") }

                    LaunchedEffect(event) {
                        dataViewModel.getOrganizerName(event, callback = { name ->
                            organizerName = name ?: ""
                        })
                    }

                    ListItem(
                        overlineContent = { Text(event.date) },
                        headlineContent = { Text(event.title) },
                        supportingContent = { Text(organizerName)},
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_today_24),
                                contentDescription = "Localized description",
                                tint = colorResource(R.color.dark_blue)
                            )
                        },
                        trailingContent = {
                            if (event.organizer == Firebase.auth.currentUser?.uid) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Localized description",
                                    tint = colorResource(R.color.dark_blue),
                                    modifier = Modifier.clickable {
                                        showDialog = true
                                    }
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("eventDetail/${event.id}")
                            isClicked = !isClicked
                        }
                    )
                    if (isClicked) {
                        HorizontalDivider(
                            color = colorResource(R.color.red),
                            thickness = 1.dp,
                            modifier = Modifier
                        )
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(text = "Event löschen") },
                            text = {
                                Text(text = "Willst du das Event wirklich löschen?")
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    dataViewModel.deleteEvent(event)
                                    showDialog = false
                                }) {
                                    Text("Löschen")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Abbrechen")
                                }
                            },
                            containerColor = colorResource(R.color.light_grey)
                        )
                    }
                }
            }
        }
    }
}
