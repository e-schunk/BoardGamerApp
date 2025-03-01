package com.example.boardgame.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.boardgame.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BoardGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(), // ✅ Nur das helle Theme verwenden
        typography = Typography,
        content = content
    )
}

@Composable
fun customTextFieldColors() = TextFieldDefaults.colors(
    unfocusedLabelColor = colorResource(R.color.dark_blue),
    focusedLabelColor = colorResource(R.color.light_blue),
    unfocusedIndicatorColor = colorResource(R.color.dark_blue),
    focusedIndicatorColor = colorResource(R.color.light_blue),
    cursorColor = colorResource(R.color.dark_blue),
    focusedTextColor = colorResource(R.color.dark_blue),
    unfocusedTextColor = colorResource(R.color.dark_blue),
    unfocusedContainerColor = colorResource(R.color.white),
    focusedContainerColor = colorResource(R.color.light_grey)
)