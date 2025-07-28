package de.eduardschunk.boardplay.pages.events

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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import de.eduardschunk.boardplay.data.Event
import de.eduardschunk.boardplay.data.User
import de.eduardschunk.boardplay.ui.theme.RobotoFontFamily

@Composable
fun GamerListPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    dataViewModel: DataViewModel,
    eventId: String?
) {
    val authState = authViewModel.authState.observeAsState()
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    var event by remember { mutableStateOf(Event()) }
    var organizerName by remember { mutableStateOf("") }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("home")
            else -> Unit
        }
    }

    LaunchedEffect(eventId) {
        userList = eventId?.let { dataViewModel.fetchUserListByEventId(it) } ?: emptyList()

        dataViewModel.fetchEventById(eventId) { loadedEvent ->
            if (loadedEvent != null) {
                event = loadedEvent
            }
        }
    }

    LaunchedEffect(event) {
        dataViewModel.getOrganizerName(event, callback = { organizerName = it ?: "" })
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titel = "Teilnehmer",
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
                    "Teilnehmer", style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.black),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(39.dp)
                )
                IconButton(onClick = {
                    navController.navigate("gamerSuggest/${event.id}")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Add",
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }
            ListItem(
                headlineContent = { Text("$organizerName (Gastgeber)") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.AccountBox,
                        contentDescription = "Profile",
                        Modifier.size(22.dp)
                    )
                },
            )
            HorizontalDivider(
                color = colorResource(R.color.yellow),
                thickness = 1.dp,
                modifier = Modifier
            )
            if (userList.isEmpty()) {
                Spacer(modifier = Modifier.height(50.dp))
                Text( text = "Keine Teilnehmer",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.dark_grey),
                        fontFamily = RobotoFontFamily
                    ),
                    modifier = Modifier.height(28.dp))
            }
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(userList) { user ->
                    var userState by remember { mutableStateOf(UserState.PendingInvitation) }

                    val userDisplayName = user.firstName + " " + user.lastName

                    LaunchedEffect(user, event) {
                        userState = dataViewModel.checkUserState(user, event) ?: UserState.PendingInvitation
                    }

                    val stateIcon: ImageVector = when (userState) {
                        UserState.AcceptInvitation -> {
                            Icons.Filled.CheckCircleOutline
                        }
                        UserState.DeclinedInvitation -> {
                            Icons.Filled.Block
                        }
                        else -> {
                            Icons.Outlined.PendingActions
                        }
                    }

                    ListItem(
                        headlineContent = { Text(userDisplayName) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile",
                                Modifier.size(22.dp)
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = stateIcon,
                                contentDescription = "state",
                                Modifier.size(22.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

enum class UserState {
    PendingInvitation,
    AcceptInvitation,
    DeclinedInvitation
}