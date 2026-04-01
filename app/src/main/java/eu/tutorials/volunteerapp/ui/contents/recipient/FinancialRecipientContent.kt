package eu.tutorials.volunteerapp.ui.contents.recipient

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help.Financial
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.DateRangePicker
import eu.tutorials.volunteerapp.ui.components.outlinedField
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FinancialRecipientContent(viewModel: MainViewModel, navController: NavController, userId: Int) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }
    var planned_amount by remember { mutableStateOf("") }


    // Завантаження даних користувача
    LaunchedEffect(userId) {
        viewModel.getUserById(userId) {
            user = it
            isLoading = false
        }
    }

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = this.maxWidth

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Фінансова допомога",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp,
                        color = Color(Colors.DarkBlue.rgb),
                        fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Назва",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color(Colors.DarkBlue.rgb),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.padding(4.dp).align(Alignment.Start)
                    )
                    Column {
                        outlinedField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Назва",
                            width = maxWidth
                        )
                        titleError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Опис",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color(Colors.DarkBlue.rgb),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.padding(4.dp).align(Alignment.Start)
                    )
                    Column {
                        outlinedField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Опис",
                            width = maxWidth
                        )
                        descriptionError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                DateRangePicker(
                    fromDate = fromDate,
                    toDate = toDate,
                    onFromDateChange = { fromDate = it },
                    onToDateChange = { toDate = it }
                )
                dateError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Планова сума",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color(Colors.DarkBlue.rgb),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.padding(4.dp).align(Alignment.Start)
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = planned_amount,
                                onValueChange = {
                                    planned_amount = it.filter { char ->
                                        char.isDigit() || char == '.'
                                    }
                                },
                                label = {
                                    Text(
                                        text = "Планова сума",
                                        color = Color(Colors.DarkBlue.rgb)
                                    )
                                },
                                modifier = Modifier
                                    .width(330.dp)
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color(Colors.LightBlue.rgb),
                                    unfocusedIndicatorColor = Color.Gray,
                                    cursorColor = Color(Colors.DarkBlue.rgb),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "грн",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    color = Color(Colors.DarkBlue.rgb),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        amountError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }


            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Button(
                    onClick = {
                        titleError = null
                        descriptionError = null
                        dateError = null
                        amountError = null

                        var hasError = false

                        if (title.isBlank()) {
                            titleError = "Введіть назву"
                            hasError = true
                        }
                        if (description.isBlank()) {
                            descriptionError = "Введіть опис"
                            hasError = true
                        }
                        if (fromDate == null) {
                            dateError = "Введіть дату початку"
                            hasError = true
                        }
                        if (toDate == null) {
                            dateError = "Введіть дату кінця"
                            hasError = true
                        }
                        val amount = planned_amount.toDoubleOrNull()
                        if (amount == null) {
                            amountError = "Введіть коректну планову суму"
                            hasError = true
                        } else if (amount > 20000.0) {
                            amountError = "Планова сума має бути менша 20 000"
                            hasError = true
                        }

                        if (!hasError) {
                            val help = Financial(
                                title = title,
                                description = description,
                                idRecipient = userId,
                                from = fromDate ?: LocalDate.now(),
                                to = toDate ?: LocalDate.now().plusDays(30),
                                plannedAmount = planned_amount.toDouble(),
                                createdAt = LocalDateTime.now()
                            )

                            viewModel.createHelp(help) { createdHelp ->
                                if (createdHelp != null) {
                                    Toast.makeText(
                                        context,
                                        "Допомогу успішно створено!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    title = ""
                                    description = ""
                                    fromDate = null
                                    toDate = null
                                    planned_amount = ""
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Не вдалося створити допомогу",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(Colors.MainBlue.rgb),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Text(
                        text = "Створити",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}