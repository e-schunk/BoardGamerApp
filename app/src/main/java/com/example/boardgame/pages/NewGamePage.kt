package com.example.boardgame.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import com.example.boardgame.AuthState
import com.example.boardgame.AuthViewModel
import com.example.boardgame.DataViewModel
import com.example.boardgame.R
import com.example.boardgame.components.AppTopBar
import com.example.boardgame.data.Event
import com.example.boardgame.data.Game
import com.example.boardgame.encodeBitmapToBase64
import com.example.boardgame.ui.theme.RobotoFontFamily
import com.example.boardgame.ui.theme.customTextFieldColors
import com.example.boardgame.uriToBitmap


@Composable
fun NewGamePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel,
    eventId: String?
) {

    val authState = authViewModel.authState.observeAsState()
    var event by remember { mutableStateOf<Event?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var image by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            uri?.let {
                val bitmap = uriToBitmap(context, it)
                image = encodeBitmapToBase64(bitmap)
            }
        }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }

        dataViewModel.fetchEventById(eventId) { loadedEvent ->
            event = loadedEvent
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Neues Spiel",
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
                "Neues Spiel anlegen", style = TextStyle(
                    fontSize = 28.sp,
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
                    .height(60.dp)
            )
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonColors(
                    containerColor = colorResource(R.color.light_blue),
                    contentColor = colorResource(R.color.white),
                    disabledContainerColor = colorResource(R.color.dark_blue),
                    disabledContentColor = colorResource(R.color.white)
                ),
                modifier = modifier
                    .width(275.dp)
                    .height(60.dp),
            ) {
                Text("Bild auswählen")
            }
            FilledIconButton(
                onClick = {
                    val game = Game(
                        title = title,
                        description = desc,
                        image = image ?: ""
                    )
                    dataViewModel.saveGame(
                        game,
                        onSuccess = {
                            navController.navigate("gameList/${event?.id}")
                        },
                        onFailure = { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(275.dp)
                    .height(60.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = colorResource(R.color.dark_blue),
                    contentColor = colorResource(R.color.white)
                )
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save icon",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = colorResource(R.color.white)
                    )
                    Text(
                        text = "Speichern",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}