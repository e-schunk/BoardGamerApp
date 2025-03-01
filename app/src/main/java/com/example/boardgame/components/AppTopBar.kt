package com.example.boardgame.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.boardgame.DataViewModel
import com.example.boardgame.R
import com.example.boardgame.ui.theme.MontserratFontFamily
import com.example.boardgame.ui.theme.RobotoFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(titel: String, dataViewModel: DataViewModel, onMenuClick: () -> Unit) {
    val userId = Firebase.auth.currentUser?.uid
    var displayName by remember { mutableStateOf("") }

    dataViewModel.fetchUserById(userId, callback = { user ->
        displayName = if (user == null) { titel } else { user.firstName + " " + user.lastName }
    })

    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = displayName,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily,
                    )
                )
                Text(
                    "BoardPlay",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.black),
                        fontFamily = MontserratFontFamily,
                    ),
                    modifier = Modifier
                        .clickable(onClick = onMenuClick)
                        .padding(end = 16.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.bg_appbar_blue),
            titleContentColor = colorResource(R.color.black)
        )
    )
}