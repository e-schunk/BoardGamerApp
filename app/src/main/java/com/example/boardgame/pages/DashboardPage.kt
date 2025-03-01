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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.boardgame.ui.theme.RobotoFontFamily


@Composable
fun DashboardPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Dashboard",
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
                text = "Dashboard",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black),
                    fontFamily = RobotoFontFamily
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(39.dp)
            )
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
                            navController.navigate("eventList")
                        },
                        containerColor = colorResource(R.color.dark_blue)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = "Event button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(42.dp)
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

                Column {
                    LargeFloatingActionButton(
                        onClick = {
                            navController.navigate("invitation")
                        },
                        containerColor = colorResource(R.color.dark_blue)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContactMail,
                            contentDescription = "Invitation button",
                            tint = colorResource(R.color.white),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Text(
                        text = "Einladungen",
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