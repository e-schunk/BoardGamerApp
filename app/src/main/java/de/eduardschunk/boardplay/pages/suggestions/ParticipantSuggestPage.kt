package de.eduardschunk.boardplay.pages.suggestions

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import de.eduardschunk.boardplay.data.SuggestionDate
import de.eduardschunk.boardplay.data.User
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantSuggestPage(
    modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, dataViewModel: DataViewModel, suggestionDateId: String?
) {

    val authState = authViewModel.authState.observeAsState()
    var suggestionDate by remember { mutableStateOf<SuggestionDate?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<User>>(emptyList()) }
    var isActive by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(suggestionDateId) {
        suggestionDate = suggestionDateId?.let { dataViewModel.fetchSuggestionDateById(suggestionDateId) } ?: SuggestionDate()
    }

    Scaffold(topBar = {
        AppTopBar(
            titel = "Umfrage", onMenuClick = { navController.navigate("home") }, dataViewModel = dataViewModel
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
                    "Person zur Umfrage einladen", style = TextStyle(
                        fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.black), fontFamily = RobotoFontFamily
                    ), modifier = Modifier.height(39.dp)
                )
            }
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (it.isNotEmpty() && it.length >= 3) {
                        dataViewModel.searchInUsers(it) { users ->
                            searchResult = users
                        }
                    } else {
                        searchResult = emptyList()
                    }
                },
                onSearch = { isActive = false },
                active = isActive,
                onActiveChange = { isActive = it },
                placeholder = { Text("Gib den Namen ein...") },
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
                    items(searchResult) { userItem ->
                        ListItem(headlineContent = { Text(userItem.firstName + " " + userItem.lastName) }, modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (suggestionDateId != null) {
                                    showDialog = true
                                    selectedUser = userItem
                                }
                            })
                        HorizontalDivider(
                            color = colorResource(R.color.green), thickness = 1.dp, modifier = Modifier
                        )
                    }
                }
                if (showDialog && selectedUser != null && suggestionDateId != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false }, title = { Text(text = "Teilnehmer hinzufügen") }, text = {
                        Text(text = "Willst du den Teilnehmer ${selectedUser!!.firstName} ${selectedUser!!.lastName} wirklich hinzufügen?")
                    }, confirmButton = {
                        TextButton(
                            onClick = {
                                dataViewModel.addPersonToSuggestionDate(suggestionDateId = suggestionDateId, userId = selectedUser!!.id, onSuccess = {
                                    Toast.makeText(
                                        navController.context, "Person hinzugefügt", Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("suggestionDateDetail/$suggestionDateId")
                                }, onFailure = {
                                    Toast.makeText(
                                        navController.context, "Person konnte nicht hinzugefügt werden", Toast.LENGTH_SHORT
                                    ).show()
                                })
                                showDialog = false
                            }) {
                            Text("Hinzufügen")
                        }
                    }, dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Abbrechen")
                        }
                    }, containerColor = colorResource(R.color.light_grey)
                    )
                }
            }
        }
    }
}
