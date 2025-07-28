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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
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
fun SuggestionDatesViewPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel,
    suggestionDateId: String?
) {
    val authState = authViewModel.authState.observeAsState()
    var suggestionDate by remember { mutableStateOf(SuggestionDate()) }
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
                    var isChecked by remember { mutableStateOf(false) }
                    ListItem(
                        headlineContent = { Text(entry.date) },
                        leadingContent = {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    isChecked = !isChecked
                                    if (isChecked) {
                                        dataViewModel.addDateAndUserToDatesOnSuggestionDate(suggestionDateId!!, entry, Firebase.auth.currentUser!!.uid)
                                        dataViewModel.updateDateEntryCounter(entry.key, suggestionDateId, true)
                                    } else {
                                        dataViewModel.removeDateAndUserFromDatesOnSuggestionDate(suggestionDateId!!, entry.key)
                                        dataViewModel.updateDateEntryCounter(entry.key, suggestionDateId, false)
                                    }
                                    dataViewModel.updateParticipantState(Firebase.auth.currentUser!!.uid, suggestionDateId, ParticipantState.HasVoted)
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        trailingContent = {
                            Text(
                                text = entry.counter.toString(),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = colorResource(R.color.ic_launcher_background),
                                    fontFamily = RobotoFontFamily
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}