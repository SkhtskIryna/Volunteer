package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.PaymentActivity
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.findActivity
import eu.tutorials.volunteerapp.ui.components.UserAvatar
import eu.tutorials.volunteerapp.ui.components.loadCardNumberFromFile
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FinancialHelpDetailsForVolunteer(
    helpId: Int,
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val help by viewModel.getHelpByIdFlow(helpId).collectAsState()
    var recipient by remember { mutableStateOf<User?>(null) }
    var amountText by remember { mutableStateOf("") }
    var recipientCardNumber by remember { mutableStateOf("") }
    val clientToken by viewModel.clientToken.collectAsState()

    LaunchedEffect(help?.idRecipient) {
        help?.idRecipient?.let { recipientId ->
            viewModel.getUserById(recipientId) { user ->
                recipient = user
                user?.id?.let { id ->
                    val loadedNumber = loadCardNumberFromFile(context, id)
                    recipientCardNumber = loadedNumber?.filter { it.isDigit() } ?: ""
                }
            }
        }
    }

    val activity = LocalContext.current.findActivity()
    val amountUAH = amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO

    LaunchedEffect(clientToken) {
        clientToken?.let { token ->
            Log.d("PaymentDebug", "Navigating to PaymentActivity: $token")
            activity?.let {
                val intent = Intent(context, PaymentActivity::class.java).apply {
                    putExtra("CHECKOUT_URL", token)
                }
                it.startActivity(intent)
                viewModel.clearClientToken()
            } ?: Log.e("PaymentDebug", "Activity is null! Cannot navigate")
        }
    }

    help?.let { h ->
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Кнопка назад поза рамкою
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color(Colors.DarkBlue.rgb)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        recipient?.let { user ->
                            UserAvatar(
                                photoBase64 = user.photoBase64,
                                firstName = user.firstName,
                                lastName = user.lastName
                            )
                        } ?: Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(Colors.MainBlue.rgb))
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = recipient?.let { "${it.lastName} ${it.firstName}" }
                                ?: "Завантаження...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Заголовок
                    Text(
                        text = h.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color(Colors.DarkBlue.rgb)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Дати
                    Text(
                        text = "Створено: ${h.createdAt.format(formatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(Colors.DarkBlue.rgb)
                    )

                    h.updatedAt?.let {
                        Text(
                            text = "Оновлено: ${it.format(formatter)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    (h as? Help.Financial)?.let { financial ->
                        Text(
                            text = "Період: ${h.from} – ${h.to}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Планова сума: ${h.plannedAmount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )

                        Text(
                            text = "Отримано: ${h.collected ?: 0.0}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    h.description.takeIf { it.isNotBlank() }?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    val context = LocalContext.current

                    Column(modifier = Modifier.fillMaxWidth()) {

                        // Поле для суми
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = amountText,
                                onValueChange = { amountText = it.filter { it.isDigit() } },
                                placeholder = { Text("Сума") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("UAH", fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(8.dp))

                        // Кнопки швидких сум
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(200, 500, 1000).forEach { quickAmount ->
                                Button(
                                    onClick = { amountText = quickAmount.toString() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(Colors.MainBlue.rgb),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("+$quickAmount")
                                }
                            }
                        }

                        Button(
                            onClick = {
                                Log.d("PaymentDebug", "Button clicked, amountUAH = $amountUAH")

                                if (amountUAH > BigDecimal.ZERO) {
                                    Log.d("PaymentDebug", "Calling fetchClientToken for amount: $amountUAH")
                                    viewModel.startDonation(amountUAH.toDouble(), helpId)
                                } else {
                                    Toast.makeText(context, "Введіть суму для оплати", Toast.LENGTH_SHORT).show()
                                    Log.d("PaymentDebug", "Amount is zero or invalid")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(Colors.MainBlue.rgb),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Оплатити $amountUAH UAH")
                        }
                    }
                }
            }
        }
    }
}
