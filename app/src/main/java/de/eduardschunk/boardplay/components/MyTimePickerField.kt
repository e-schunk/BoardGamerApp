package de.eduardschunk.boardplay.components

import android.app.TimePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import de.eduardschunk.boardplay.R
import de.eduardschunk.boardplay.ui.theme.customTextFieldColors
import java.util.Calendar
import java.util.Locale

@Composable
fun MyTimePickerField(
    timeText: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    TextField(
        value = timeText,
        onValueChange = { _: String -> },
        modifier = modifier,
        readOnly = true,
        label = { Text("Uhrzeit") },
        trailingIcon = {
            IconButton(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            // Formatierung der Uhrzeit als HH:MM
                            val formatedTime = String.format(Locale.GERMANY,"%02d:%02d", hour, minute)
                            onDateChange(formatedTime)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Uhrzeit auswählen",
                    tint = colorResource(R.color.dark_blue)
                )
            }
        },
        colors = customTextFieldColors()
    )
}
