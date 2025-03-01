package com.example.boardgame.components

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.boardgame.DataViewModel
import com.example.boardgame.data.Event

@Composable
fun UserInvitationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    event: Event,
    dataViewModel: DataViewModel
) {
    if (showDialog) {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Einladung") },
            text = { Text(text = "Möchtest du die Einladung annehmen?") },
            confirmButton = {
                TextButton(onClick = {
                    dataViewModel.acceptInvitation(event)
                    onDismiss()
                    Toast.makeText(
                        context,
                        "Einladung akzeptiert!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text("Annehmen")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dataViewModel.declineInvitation(event)
                    onDismiss()
                    Toast.makeText(
                        context,
                        "Einladung abgelehnt!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text("Ablehnen")
                }
            }
        )
    }
}
