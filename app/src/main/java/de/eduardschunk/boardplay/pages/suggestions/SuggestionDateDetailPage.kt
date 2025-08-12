package de.eduardschunk.boardplay.pages.suggestions

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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Today
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import de.eduardschunk.boardplay.AuthState
import de.eduardschunk.boardplay.AuthViewModel
import de.eduardschunk.boardplay.DataViewModel
import de.eduardschunk.boardplay.R
import de.eduardschunk.boardplay.components.AppTopBar
import de.eduardschunk.boardplay.data.SuggestionDate
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuggestionDateDetailPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel,
    suggestionDateId: String
) {
    val authState = authViewModel.authState.observeAsState()
    var suggestionDate by remember { mutableStateOf<SuggestionDate?>(null) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(suggestionDateId) {
        suggestionDate = suggestionDateId.let { dataViewModel.fetchSuggestionDateById(suggestionDateId) } ?: SuggestionDate()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Terminabstimmung Details",
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
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = suggestionDate?.title ?: "Titel fehlt",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black),
                    fontFamily = RobotoFontFamily
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            suggestionDate?.description?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .padding(top = 80.dp, bottom = 20.dp)
            )
            {
                Column {
                    LargeFloatingActionButton(
                        onClick = {
                            navController.navigate("participantList/$suggestionDateId")
                        },
                        containerColor = colorResource(R.color.dark_blue),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = "Gamer button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Teilnehmer",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = RobotoFontFamily,
                            color = colorResource(R.color.black)
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Column {
                    LargeFloatingActionButton(
                        onClick = {
                            if (Firebase.auth.currentUser?.uid == suggestionDate?.organizer) {
                                navController.navigate("suggestionEditDates/$suggestionDateId")
                            } else {
                                navController.navigate("suggestionViewDates/$suggestionDateId")
                            }
                        },
                        containerColor = colorResource(R.color.dark_blue),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Today,
                            contentDescription = "Date button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Termine",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = RobotoFontFamily,
                            color = colorResource(R.color.black)
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
