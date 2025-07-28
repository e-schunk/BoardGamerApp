package de.eduardschunk.boardplay.pages.suggestions

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
import androidx.compose.material.icons.filled.Ballot
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
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import de.eduardschunk.boardplay.data.SuggestionDate
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuggestionDateListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    var suggestDateList by remember { mutableStateOf<List<SuggestionDate>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(showDialog) {
        dataViewModel.fetchSuggestions { suggestions ->
            suggestDateList = suggestions
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Terminvorschläge",
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
                    "Terminvorschläge", style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(39.dp)
                )
                IconButton(onClick = {
                    navController.navigate("newSuggestionDate")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Add",
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            if (suggestDateList.isEmpty()) {
                Spacer(modifier = Modifier.height(50.dp))
                Text( text = "Du hast keine Terminvorschläge",
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
                items(suggestDateList) { suggestionDate ->
                    var organizerName by remember { mutableStateOf("") }

                    LaunchedEffect(suggestionDate) {
                        dataViewModel.getOrganizerName(suggestionDate, callback = { name ->
                            organizerName = name ?: ""
                        })
                    }

                    ListItem(
                        overlineContent = { Text(Date(suggestionDate.createdAt).toString()) },
                        headlineContent = { Text(suggestionDate.title) },
                        supportingContent = { Text(organizerName)},
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Ballot,
                                contentDescription = "suggestion date icon",
                                tint = colorResource(R.color.dark_blue)
                            )
                        },
                        trailingContent = {
                            if (suggestionDate.organizer == Firebase.auth.currentUser?.uid) {
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
                            navController.navigate("suggestionDateDetail/${suggestionDate.id}")
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
                            title = { Text(text = "Terminvorschlag löschen") },
                            text = {
                                Text(text = "Willst du den Terminvorschlag wirklich löschen?")
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    dataViewModel.deleteSuggestion(suggestionDate)
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
