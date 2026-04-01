package eu.tutorials.volunteerapp.ui.components

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateRangePicker(
    fromDate: LocalDate?,
    toDate: LocalDate?,
    onFromDateChange: (LocalDate) -> Unit,
    onToDateChange: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Вибір From дати
        OutlinedTextField(
            value = fromDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    val today = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            onFromDateChange(LocalDate.of(year, month + 1, dayOfMonth))
                        },
                        today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Вибрати дату"
                    )
                }
            },
            label = { Text("Від") },
            modifier = Modifier.weight(1f)
        )

        // Вибір To дати
        OutlinedTextField(
            value = toDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    val today = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            onToDateChange(LocalDate.of(year, month + 1, dayOfMonth))
                        },
                        today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Вибрати дату"
                    )
                }
            },
            label = { Text("До") },
            modifier = Modifier.weight(1f)
        )
    }
}