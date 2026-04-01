package eu.tutorials.volunteerapp.ui.startpages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.UserRole
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.components.outlinedField
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun SignIn (viewModel: MainViewModel, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var emailError by remember { mutableStateOf<String?>(null) }
        var passwordError by remember { mutableStateOf<String?>(null) }

        Column(modifier = Modifier
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.Start).padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color(Colors.DarkBlue.rgb)
                )
            }

            Text(
                "Вхід", style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 35.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.padding(16.dp))

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

            Button(onClick = {
                emailError = null
                passwordError = null

                var hasError = false

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

                if (!hasError) {
                    viewModel.authenticateUser(email, password) { success, role ->
                        if (success && role != null) {

                            viewModel.userViewModel.findUserByEmail(email) { user ->

                                if (user == null) {
                                    Toast.makeText(context, "Помилка користувача", Toast.LENGTH_LONG).show()
                                    return@findUserByEmail
                                }

                                if (user.isBlocked!!) {
                                    Toast.makeText(
                                        context,
                                        "Ваш акаунт заблоковано",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@findUserByEmail
                                }

                                viewModel.updateCurrentUserId(user.id)

                                Toast.makeText(context, "Вхід успішний!", Toast.LENGTH_LONG).show()

                                when (role) {
                                    UserRole.Recipient -> {
                                        navController.navigate("financial_recipient/${user.id}") {
                                            popUpTo("sign_in") { inclusive = true }
                                        }
                                    }

                                    UserRole.Admin -> {
                                        navController.navigate("requests_for_help/${user.id}") {
                                            popUpTo("sign_in") { inclusive = true }
                                        }
                                    }

                                    UserRole.Volunteer -> {
                                        navController.navigate("financial_volunteer/${user.id}") {
                                            popUpTo("sign_in") { inclusive = true }
                                        }
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(context, "Невірна пошта або пароль!", Toast.LENGTH_LONG).show()
                        }

                        email = ""
                        password = ""
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
                Text(text = "Увійти", style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    color = Color(Color.White.value),
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier.padding(5.dp))
            }
        }
    }
}