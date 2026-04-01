package eu.tutorials.volunteerapp.ui.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.formatPhone
import eu.tutorials.volunteerapp.ui.components.getInitials
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun Profile(viewModel: MainViewModel, navController: NavController, userId: Int, cardId: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        var user by remember { mutableStateOf<User?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        // Завантаження даних користувача
        LaunchedEffect(userId) {
            viewModel.getUserById(userId) {
                user = it
                isLoading = false
            }
        }

        val avatarBitmap = remember(user?.photoBase64) {
            user?.photoBase64?.takeIf { it.isNotEmpty() }?.let { base64 ->
                try {
                    val decoded = Base64.decode(base64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decoded, 0, decoded.size).asImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Верхній ряд з кнопками
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color(Colors.DarkBlue.rgb)
                    )
                }
                IconButton(onClick = {
                    user?.id?.let { id ->
                        navController.navigate("profile_settings/$id")
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Налаштування",
                        tint = Color(Colors.DarkBlue.rgb)
                    )
                }
            }

            Text(
                "Профіль",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 35.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.padding(16.dp))

            when {
                isLoading -> CircularProgressIndicator(color = Color(Colors.MainBlue.rgb))

                user != null -> {
                    val u = user!!

                    // Аватар з ініціалами
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(Colors.MainBlue.rgb)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarBitmap != null) {
                            Image(bitmap = avatarBitmap, contentDescription = "Аватар", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            androidx.compose.material.Text(
                                text = getInitials("${user?.firstName ?: ""} ${user?.lastName ?: ""}"),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("${u.firstName} ${u.lastName}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        when (u.role.name) {
                            "Volunteer" -> "Волонтер"
                            "Recipient" -> "Отримувач"
                            "Admin" -> "Адміністратор"
                            else -> u.role.name
                        },
                        color = Color.Gray,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Контактна інформація
                    ProfileInfoRow(Icons.Default.Phone, "Телефон", formatPhone(u.phone))
                    ProfileInfoRow(Icons.Default.Email, "Пошта", u.email)
                    ProfileInfoRow(Icons.Default.Send, "Телеграм", u.telegram ?: "Не вказано")

                    Spacer(modifier = Modifier.height(30.dp))

                    // Кнопка Картка тільки для Recipient
                    if (u.role.name == "Recipient") {
                        Button(
                            onClick = { navController.navigate("card_settings/${cardId}") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(Colors.MainBlue.rgb)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Картка", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Кнопка виходу
                    OutlinedButton(
                        onClick = {
                            // Скидання поточного користувача
                            viewModel.updateCurrentUserId(null)

                            // Навігація до логіну
                            navController.navigate("sign_in") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        },
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Вихід", color = Color.Red)
                    }
                }

                else -> Text(
                    "Не вдалося завантажити дані користувача",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(Colors.MainBlue.rgb),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}