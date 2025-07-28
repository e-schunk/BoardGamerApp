package de.eduardschunk.boardplay.components

import android.app.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
fun MyDatePickerField(
    dateText: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    TextField(
        value = dateText,
        onValueChange = { _: String -> },
        modifier = modifier,
        readOnly = true,
        label = { Text("Datum") },
        trailingIcon = {
            IconButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            // Formatierung: dd.MM.yyyy, mit führender 0 bei Tag und Monat
                            val formattedDate = String.format(Locale.GERMANY,"%02d.%02d.%d", dayOfMonth, month + 1, year)
                            onDateChange(formattedDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Datum auswählen",
                    tint = colorResource(R.color.dark_blue)
                )
            }
        },
        colors = customTextFieldColors()
    )
}
