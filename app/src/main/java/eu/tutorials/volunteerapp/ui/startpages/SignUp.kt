package eu.tutorials.volunteerapp.ui.startpages

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.UserRole
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.components.outlinedField
import eu.tutorials.volunteerapp.ui.components.saveCardNumberToFile
import eu.tutorials.volunteerapp.ui.components.saveCvv2ToFile
import eu.tutorials.volunteerapp.ui.navigation.signUp.LinkToAdministrators
import eu.tutorials.volunteerapp.ui.navigation.signUp.LinkToSingIn
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignUp (viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var telegram by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var hasCard by remember { mutableStateOf(false) }
    var telegramWasStarted by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cvv2 by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var telegramError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardExpiryError by remember { mutableStateOf<String?>(null) }
    var cvv2Error by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Реєстрація", style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 35.sp,
            color = Color(Colors.DarkBlue.rgb),
            fontWeight = FontWeight.Bold
        ), modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.padding(16.dp))

        Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Прізвище", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(10.dp))
            Column {
                outlinedField(lastName, { lastName = it }, "Last Name", 220.dp)
                lastNameError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Ім'я", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(10.dp))
            Column {
                outlinedField(firstName, { firstName = it }, "First Name", 220.dp)
                firstNameError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Телефон", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(10.dp))
            Column {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter { char -> char.isDigit() } },
                    label = {
                        Text(
                            text = "Phone",
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    },
                    modifier = Modifier
                        .width(220.dp)
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
                phoneError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Пошта", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(10.dp))
            Column {
                outlinedField(email, { email = it }, "Email", 220.dp)
                emailError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Telegram", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(10.dp))
            Column{
                outlinedField(telegram, { telegram = it }, "Telegram", 220.dp)
                telegramError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Пароль", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(10.dp))
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "Password",
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(Colors.LightBlue.rgb),
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color(Colors.DarkBlue.rgb),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                passwordError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Text(
                text = "Підтвердження пароля",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(10.dp)
            )
            Column {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(
                            text = "Confirm Password",
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(Colors.LightBlue.rgb),
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color(Colors.DarkBlue.rgb),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                confirmPasswordError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Щоб зареєструвати обліковий запис отримувача, введіть дані картки.",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .border(
                width = 3.dp,
                color = Color(Colors.MainBlue.rgb),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)) {
            Text(text = "Номер картки", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(5.dp))

            Column {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it.filter { ch -> ch.isDigit() || ch == ' ' } },
                    label = { Text("Номер картки", color = Color(Colors.DarkBlue.rgb)) },
                    modifier = Modifier.width(500.dp),
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
                cardNumberError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row (modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                Column {
                    Text(text = "Термін дії", style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = Color(Colors.DarkBlue.rgb),
                        fontWeight = FontWeight.Bold
                    ), modifier = Modifier.padding(5.dp))

                    Column {
                        outlinedField(
                            value = cardExpiry,
                            onValueChange = { input ->
                                val filtered = input.filter { it.isDigit() || it == '-' }
                                cardExpiry = if (filtered.length > 7) filtered.take(7) else filtered
                            },
                            label = "YYYY-MM", 150.dp
                        )
                        cardExpiryError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }

                Column {
                    Text(text = "CVV2", style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = Color(Colors.DarkBlue.rgb),
                        fontWeight = FontWeight.Bold
                    ), modifier = Modifier.padding(5.dp))

                    Column {
                        OutlinedTextField(
                            value = cvv2,
                            onValueChange = { cvv2 = it.filter { ch -> ch.isDigit() }.take(3) },
                            label = { Text("CVV2", color = Color(Colors.DarkBlue.rgb)) },
                            modifier = Modifier.width(150.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(Colors.LightBlue.rgb),
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = Color(Colors.DarkBlue.rgb),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        cvv2Error?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }
        }

        Button(onClick = {
            firstNameError = null
            lastNameError = null
            emailError = null
            phoneError = null
            telegramError = null
            passwordError = null
            cardNumberError = null
            cardExpiryError = null
            cvv2Error = null

            // Перевірки
            var hasError = false
            if (firstName.isBlank()) { firstNameError = "Введіть ім'я"; hasError = true }
            if (lastName.isBlank()) { lastNameError = "Введіть прізвище"; hasError = true }
            if (email.isBlank()) {
                emailError = "Введіть пошту"
                hasError = true
            } else {
                val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
                if (!emailRegex.matches(email)) {
                    emailError = "Некоректний формат пошти"
                    hasError = true
                }
            }
            if (phone.isBlank()) {
                phoneError = "Введіть телефон"
                hasError = true
            } else {
                if (phone.length != 10) {
                    phoneError = "Повинно бути 10 цифр"
                    hasError = true
                } else if (!phone.all { it.isDigit() }) {
                    phoneError = "Телефон повинен містити лише цифри"
                    hasError = true
                }
            }
            if (telegram.isNotBlank()) {
                val trimmed = telegram.trim()
                val telegramRegex = Regex("^@[A-Za-z0-9_]{5,32}$")
                val onlyDigits = trimmed.drop(1).all { it.isDigit() }

                telegramError = when {
                    !trimmed.startsWith("@") -> "Telegram має починатися з @"
                    onlyDigits -> "Не може містити тільки цифри"
                    !telegramRegex.matches(trimmed) -> "Має бути 5–32 символи"
                    else -> null
                }
                if (telegramError != null) hasError = true
            } else if (telegramWasStarted) {
                telegramError = "Введіть Telegram або приберіть поле повністю"
                hasError = true
            } else {
                telegramError = null
            }
            if (password.isBlank()) {
                passwordError = "Введіть пароль"
                hasError = true
            } else {
                val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|]).{8,}$")
                if (!passwordRegex.matches(password)) {
                    passwordError = "Пароль має містити ≥8 символів"
                    hasError = true
                }
            }

            if (confirmPassword.isBlank()) {
                confirmPasswordError = "Підтвердіть пароль"
                hasError = true
            } else if (password != confirmPassword) {
                confirmPasswordError = "Паролі не співпадають"
                hasError = true
            } else {
                confirmPasswordError = null
            }

            val cardNumberClean = cardNumber.replace(" ", "")
            val isAnyCardFieldFilled = cardNumberClean.isNotBlank() || cardExpiry.isNotBlank() || cvv2.isNotBlank()

            if (isAnyCardFieldFilled) {
                hasCard = true

                if (cardNumberClean.isBlank()) {
                    cardNumberError = "Введіть номер картки"; hasError = true
                } else if (!cardNumberClean.matches(Regex("^\\d{16}$"))) {
                    cardNumberError = "Номер картки має містити рівно 16 цифр"; hasError = true
                }

                if (cardExpiry.isBlank()) {
                    cardExpiryError = "Введіть термін дії"; hasError = true
                } else if (!cardExpiry.matches(Regex("^\\d{4}-\\d{2}$"))) {
                    cardExpiryError = "Некоректний формат"
                    hasError = true
                } else {
                    val (year, month) = cardExpiry.split("-").map { it.toIntOrNull() ?: 0 }
                    if (month !in 1..12) {
                        cardExpiryError = "Некоректний місяць"; hasError = true
                    }
                }

                if (cvv2.isBlank()) {
                    cvv2Error = "Введіть CVV2"; hasError = true
                } else if (!cvv2.matches(Regex("^\\d{3}$"))) {
                    cvv2Error = "CVV2 має містити 3 цифри"; hasError = true
                }
            } else {
                hasCard = false
            }

            if (!hasError) {
                if (cardNumber.isNotBlank() && cardExpiry.isNotBlank() && cvv2.isNotBlank()) {
                    hasCard = true
                    UserRole.Recipient
                } else {
                    hasCard = false
                    UserRole.Volunteer
                }

                viewModel.createUser(
                    firstName, lastName, email, phone,
                    if (telegram.isBlank()) null else telegram,
                    password, hasCard
                ) { createdUserId: Int? ->
                    if (createdUserId != null) {
                        // Користувач створений
                        if (hasCard) {
                            val cvv = cvv2.toString()
                            viewModel.createCard(
                                maskedNumber = cardNumber,
                                validityPeriod = try { YearMonth.parse(cardExpiry) } catch (e: Exception) { YearMonth.now() },
                                cvv2 = cvv.toInt(),
                                idRecipient = createdUserId
                            )

                            Log.e("CVV2", cvv2)
                            saveCvv2ToFile(context, createdUserId, cvv2)
                            saveCardNumberToFile(context, createdUserId, cardNumber)
                        }
                        Toast.makeText(context, "Користувача / карту успішно створено!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Користувач з такою поштою або телефоном вже зареєстрований!", Toast.LENGTH_LONG).show()
                    }

                    // Очистка полів
                    firstName = ""
                    lastName = ""
                    email = ""
                    phone = ""
                    telegram = ""
                    password = ""
                    confirmPassword = ""
                    hasCard = false
                    cardNumber = ""
                    cardExpiry = ""
                    cvv2 = ""
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
            shape = RoundedCornerShape(17.dp)) {
            Text(text = "Зареєструватися", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = Color(Color.White.value),
                fontWeight = FontWeight.Bold
            ), modifier = Modifier.padding(5.dp))
        }

        Spacer(modifier = Modifier.padding(5.dp))
        LinkToSingIn(navController)
        LinkToAdministrators(navController)
    }
}