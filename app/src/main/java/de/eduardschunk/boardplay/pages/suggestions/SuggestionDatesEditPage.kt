package de.eduardschunk.boardplay.pages.suggestions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.platform.LocalContext
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
import de.eduardschunk.boardplay.data.SuggestionDate
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuggestionDatesEditPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel,
    suggestionDateId: String?
) {
    val authState = authViewModel.authState.observeAsState()
    var suggestionDate by remember { mutableStateOf(SuggestionDate()) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateList by dataViewModel.dateListLiveData.observeAsState(emptyList())

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(suggestionDateId) {
        suggestionDateId?.let {
            dataViewModel.fetchSuggestionDateById(it) { loadedSuggestionDate ->
                if (loadedSuggestionDate != null) {
                    suggestionDate = loadedSuggestionDate
                }
            }
            dataViewModel.observeDateList(it)
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
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            // Date wurde gewählt, jetzt TimePicker zeigen!
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    // Datum und Zeit formatieren
                                    val formattedDateTime = String.format(
                                        Locale.GERMANY,
                                        "%02d.%02d.%d %02d:%02d",
                                        dayOfMonth, month + 1, year, hourOfDay, minute
                                    )
                                    suggestionDateId?.let {
                                        // Speichere das kombinierte Datum + Zeit
                                        dataViewModel.addDateToDateListOnSuggestionDate(
                                            it,
                                            formattedDateTime
                                        )
                                    }
                                },
                                16, 0, true // Standardzeit z.B. 12:00, 24h-Format
                            ).show()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Add",
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            if (dateList.isEmpty()) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Keine Terminvorschläge",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.dark_grey),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(dateList) { entry ->
                    ListItem(
                        headlineContent = { Text(entry.date) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Today,
                                contentDescription = "Date",
                                Modifier.size(22.dp)
                            )
                        },
                        trailingContent = {
                            Row {
                                Text(
                                    text = entry.counter.toString(),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = colorResource(R.color.ic_launcher_background),
                                        fontFamily = RobotoFontFamily
                                    ),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )

                                IconButton(onClick = {
                                    suggestionDateId?.let {
                                        dataViewModel.deleteDateEntry(it, entry.key)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}