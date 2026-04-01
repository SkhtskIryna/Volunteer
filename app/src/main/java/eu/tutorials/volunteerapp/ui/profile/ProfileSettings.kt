package eu.tutorials.volunteerapp.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.getInitials
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.io.ByteArrayOutputStream

@Composable
fun ProfileSettings(viewModel: MainViewModel, navController: NavController, userId: Int) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Змінні для редагування
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telegram by remember { mutableStateOf("") }
    var photoBase64  by remember { mutableStateOf("") }

    // Змінні для зміни пароля
    var changePasswordVisible by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var telegramError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        viewModel.getUserById(userId) { fetchedUser ->
            user = fetchedUser
            fetchedUser?.let { u ->
                firstName = u.firstName
                lastName = u.lastName
                phone = u.phone
                email = u.email
                telegram = u.telegram ?: ""
                photoBase64 = u.photoBase64 ?: ""
            }
            isLoading = false
        }
    }

    val context = LocalContext.current

    // Змінна для аватара, який слідкує за photoBase64
    val avatarBitmap = remember(user?.photoBase64) {
        user?.photoBase64?.takeIf { it.isNotEmpty() }?.let { base64 ->
            try {
                val bytes = Base64.decode(base64, Base64.NO_WRAP) // <-- розкодовуємо
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Launcher для вибору фото
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Конвертування у Base64 без переносів рядків
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)

                // Оновлення user і локальну змінну для UI
                user = user?.copy(photoBase64 = base64)
                photoBase64 = base64

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    Scaffold{ padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(Colors.MainBlue.rgb))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()) // Додаємо скролл
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Аватар
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(Colors.MainBlue.rgb)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Показ аватара, якщо є
                        avatarBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp,
                                contentDescription = "Аватар",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            // Якщо фото немає — показ ініціалів
                            Text(
                                text = user?.let { getInitials("${it.firstName} ${it.lastName}") }
                                    ?: "",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Кнопки завантаження / видалення
                        Row(
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            // Кнопка завантаження фото
                            IconButton(onClick = { launcher.launch("image/*") }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Завантажити фото",
                                    tint = Color.White
                                )
                            }

                            // Кнопка видалення фото
                            if (user?.photoBase64 != null) {
                                IconButton(onClick = {
                                    user = user?.copy(photoBase64 = null)
                                    photoBase64 = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Видалити фото",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Основні поля
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            if (it.isNotBlank()) firstNameError = null
                        },
                        label = { Text("Ім'я", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        keyboardOptions = KeyboardOptions.Default
                    )
                    firstNameError?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            if (it.isNotBlank()) lastNameError = null
                        },
                        label = { Text("Прізвище", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        keyboardOptions = KeyboardOptions.Default
                    )
                    lastNameError?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it.filter { c -> c.isDigit() }
                            phoneError = null
                        },
                        label = { Text("Телефон", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    phoneError?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Пошта", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    emailError?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = telegram,
                        onValueChange = {
                            telegram = it
                            telegramError = null
                        },
                        label = { Text("Telegram", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    telegramError?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Зміна пароля
                    Text(
                        text = "Бажаєте змінити пароль?",
                        color = Color(Colors.DarkBlue.rgb),
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable { changePasswordVisible = !changePasswordVisible }
                            .padding(vertical = 8.dp)
                    )

                    if (changePasswordVisible) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Новий пароль", fontSize = 14.sp) },
                            textStyle = TextStyle(fontSize = 18.sp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Підтвердження пароля", fontSize = 14.sp) },
                            textStyle = TextStyle(fontSize = 18.sp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                        if (passwordError.isNotEmpty()) {
                            Text(passwordError, color = Color.Red, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            firstNameError = null
                            lastNameError = null
                            emailError = null
                            phoneError = null
                            telegramError = null
                            passwordError = ""

                            var hasError = false

                            if (firstName.isBlank()) {
                                firstNameError = "Введіть ім'я"
                                hasError = true
                            }

                            if (lastName.isBlank()) {
                                lastNameError = "Введіть прізвище"
                                hasError = true
                            }

                            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
                            if (email.isBlank() || !emailRegex.matches(email)) {
                                emailError = "Некоректний email"
                                hasError = true
                            }

                            if (phone.isBlank() || phone.length != 10) {
                                phoneError = "Повинно бути 10 цифр"
                                hasError = true
                            } else if (!phone.all { it.isDigit() }) {
                                phoneError = "Телефон повинен містити лише цифри"
                                hasError = true
                            }

                            if (telegram.isNotBlank()) {
                                val tg = telegram.trim()
                                val tgRegex = Regex("^@[A-Za-z0-9_]{5,32}$")
                                if (!tgRegex.matches(tg) || tg.drop(1).all { it.isDigit() }) {
                                    telegramError = "Формат: @username (5–32 символів)"
                                    hasError = true
                                }
                            }

                            if (changePasswordVisible) {
                                val strongPass = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|]).{8,}$")
                                if (!strongPass.matches(newPassword)) {
                                    passwordError = "Пароль має містити 8 символів, літери верх/ниж. регістру і спецсимвол"
                                    hasError = true
                                }
                                if (newPassword != confirmPassword) {
                                    passwordError = "Паролі не збігаються"
                                    hasError = true
                                }
                            }

                            if (hasError) return@Button

                            user?.let { u ->

                                val updatedUser = u.copy(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    email = email,
                                    telegram = telegram,
                                    password = if (changePasswordVisible) newPassword else null,
                                    photoBase64 = user?.photoBase64
                                )

                                Log.d("ProfileSettings", "Updating user: $updatedUser")
                                viewModel.updateUser(
                                    user = updatedUser
                                ) { success ->
                                    Log.d("ProfileSettings", "Update result: $success")
                                    if (success) {
                                        user = updatedUser
                                        firstName = updatedUser.firstName
                                        lastName = updatedUser.lastName
                                        phone = updatedUser.phone
                                        email = updatedUser.email
                                        telegram = updatedUser.telegram ?: ""
                                        photoBase64 = updatedUser.photoBase64 ?: ""
                                        navController.popBackStack()
                                    } else {
                                        passwordError = "Не вдалося оновити дані"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(Color(Colors.MainBlue.rgb))
                    ) {
                        Text("Зберегти", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }
    }
}