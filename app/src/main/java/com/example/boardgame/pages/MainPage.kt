package com.example.boardgame.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.boardgame.AuthState
import com.example.boardgame.AuthViewModel
import com.example.boardgame.R
import com.example.boardgame.ui.theme.MontserratFontFamily
import com.example.boardgame.ui.theme.RobotoFontFamily


@Composable
fun MainPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("dashboard")
            else -> Unit
        }
    }


    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.rb_33438),
            contentDescription = "Home Image"
        )
        Text(
            text = "BoardPlay",
            style = TextStyle(
                fontSize = 56.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(R.color.black),
                fontFamily = MontserratFontFamily,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Alles für den perfekten Spieleabend",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(R.color.black),
                fontFamily = RobotoFontFamily,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 11.dp)
        )
        FilledIconButton(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 100.dp)
                .width(275.dp)
                .height(60.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.dark_blue),
                contentColor = colorResource(R.color.white)
            )
        ){
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                    contentDescription = "Login",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        FilledIconButton(
            onClick = {
                navController.navigate("signup")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp)
                .width(275.dp)
                .height(60.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = colorResource(R.color.light_blue),
                contentColor = colorResource(R.color.white)
            )
        ){
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                    contentDescription = "Login",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}