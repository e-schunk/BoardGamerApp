package de.eduardschunk.boardplay.pages.suggestions

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import de.eduardschunk.boardplay.ui.theme.customTextFieldColors


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewSuggestionDatePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {

    val authState = authViewModel.authState.observeAsState()
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val context = LocalContext.current
    val user = Firebase.auth.currentUser
    val organizer = user?.uid ?: "Unbekannt"

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Neue Terminabstimmung",
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
            Text(
                "Neue Terminabstimmung anlegen", style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black),
                    fontFamily = RobotoFontFamily
                ),
                modifier = Modifier
                    .height(39.dp)
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .padding(start = 20.dp)
            )
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titel") },
                colors = customTextFieldColors(),
                modifier = modifier
                    .width(350.dp)
                    .height(60.dp)
            )
            TextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Beschreibung") },
                colors = customTextFieldColors(),
                modifier = modifier
                    .width(350.dp)
            )

            // Save&Next button
            Spacer(modifier.height(50.dp))
            FilledIconButton(
                onClick = {
                    val suggestionDate = SuggestionDate(
                        title = title,
                        description = desc,
                        organizer = organizer
                    )
                    dataViewModel.saveSuggestionDate(
                        suggestionDate,
                        onSuccess = {
                            navController.navigate("suggestionDateDetail/$suggestionDate.id")
                        },
                        onFailure = { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(140.dp)
                    .height(60.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = colorResource(R.color.dark_blue),
                    contentColor = colorResource(R.color.white)
                )
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = "Next icon",
                        tint = colorResource(R.color.white)
                    )
                    Text(
                        text = "Weiter",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}