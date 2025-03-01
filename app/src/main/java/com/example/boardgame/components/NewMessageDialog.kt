package com.example.boardgame.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.boardgame.DataViewModel
import com.example.boardgame.R
import com.example.boardgame.ui.theme.customTextFieldColors

@Composable
fun NewMessageDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    eventId: String?,
    userId: String?,
    dataViewModel: DataViewModel
) {
    if (showDialog) {
        var newMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Deine Nachricht") },
            text = {
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    colors = customTextFieldColors(),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            if (eventId != null && userId != null) {
                                dataViewModel.sendMessage(eventId, userId, newMessage)
                            }
                            newMessage = "" // Leeren nach dem Senden
                        }
                        onDismiss()
                    },
                    enabled = newMessage.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.dark_blue), // Hier die Hintergrundfarbe setzen
                        contentColor = colorResource(R.color.white), // Hier die Farbe für Icon und Text setzen
                        disabledContainerColor = Color.Gray, // Farbe wenn Button deaktiviert ist
                        disabledContentColor = Color.LightGray // Farbe für Icon und Text wenn Button deaktiviert ist
                    )
                ) {
                    Row {
                        Icon(
                            Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Senden",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Senden"
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text("Abbrechen")
                }
            },
            containerColor = colorResource(R.color.light_grey), // Hintergrundfarbe des Dialogs
        )
    }
}
