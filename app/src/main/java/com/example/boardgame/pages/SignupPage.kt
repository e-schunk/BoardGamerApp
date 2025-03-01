package com.example.boardgame.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.boardgame.AuthState
import com.example.boardgame.AuthViewModel
import com.example.boardgame.DataViewModel
import com.example.boardgame.R
import com.example.boardgame.components.AppTopBar
import com.example.boardgame.ui.theme.customTextFieldColors

@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel
) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Sign Up",
                onMenuClick = { navController.navigate("home") },
                dataViewModel = dataViewModel
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_account_circle_24),
                contentDescription = "Sign Up Icon",
                tint = colorResource(R.color.green),
                modifier = modifier.padding(vertical = 10.dp)
            )
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Vorname") },
                colors = customTextFieldColors(),
                modifier = modifier
                    .width(350.dp)
                    .height(60.dp)
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nachname") },
                colors = customTextFieldColors(),
                modifier = modifier
                    .width(350.dp)
                    .height(60.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = customTextFieldColors(),
                modifier = modifier
                    .width(350.dp)
                    .height(60.dp)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = customTextFieldColors(),
                modifier = modifier
                    .width(350.dp)
                    .height(60.dp)
            )
            Spacer(Modifier.height(30.dp))
            FilledIconButton(
                onClick = {
                    authViewModel.signup(firstName, lastName, email, password)
                },
                enabled = authState.value != AuthState.Loading,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(275.dp)
                    .height(60.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = colorResource(R.color.light_blue),
                    contentColor = colorResource(R.color.white)
                )
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                        contentDescription = "Login",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            TextButton(onClick = { navController.navigate("login") }) {
                Text(
                    text = "Schon registriert? Jetzt einloggen.",
                    color = colorResource(R.color.dark_blue)
                )

            }
        }
    }
}