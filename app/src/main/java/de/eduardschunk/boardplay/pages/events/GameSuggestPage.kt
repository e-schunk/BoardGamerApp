package de.eduardschunk.boardplay.pages.events

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.platform.LocalContext
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
import de.eduardschunk.boardplay.data.EventGame
import de.eduardschunk.boardplay.data.Game
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSuggestPage(
    modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, dataViewModel: DataViewModel, eventId: String?
) {

    val authState = authViewModel.authState.observeAsState()
    var event by remember { mutableStateOf<Event?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<Game>>(emptyList()) }
    var isActive by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
            titel = "Spiel vorschlagen", onMenuClick = { navController.navigate("home") }, dataViewModel = dataViewModel
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
                .background(colorResource(R.color.white)), horizontalAlignment = Alignment.CenterHorizontally
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
                    "Spiel vorschlagen", style = TextStyle(
                        fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.black), fontFamily = RobotoFontFamily
                    ), modifier = Modifier.height(39.dp)
                )
                IconButton(onClick = {
                    navController.navigate("newGame/$eventId")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Add",
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        dataViewModel.searchInGames(it) { games ->
                            searchResult = games
                        }
                    } else {
                        searchResult = emptyList()
                    }
                },
                onSearch = { isActive = false },
                active = isActive,
                onActiveChange = { isActive = it },
                placeholder = { Text("Suche nach einem Spiel...") },
                trailingIcon = {
                    IconButton(onClick = {
                        searchQuery = ""
                        isActive = false
                    }) { Icon(Icons.Default.Close, "Löschen") }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = colorResource(R.color.bg_appbar_blue), dividerColor = colorResource(R.color.dark_blue)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn {
                    items(searchResult) { gameItem ->
                        ListItem(
                            headlineContent = { Text(gameItem.title) },
                            supportingContent = { Text(gameItem.description) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val eventGame = EventGame(
                                        eventId = event?.id ?: "", gameId = gameItem.id
                                    )
                                    dataViewModel.saveEventGame(eventGame, onSuccess = {
                                        navController.navigate("gameList/${event?.id}")
                                    }, onFailure = { e ->
                                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                    })
                                    isActive = false
                                })
                        HorizontalDivider(
                            color = colorResource(R.color.green), thickness = 1.dp, modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}