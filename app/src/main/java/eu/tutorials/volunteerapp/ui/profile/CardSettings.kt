package eu.tutorials.volunteerapp.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.Card
import eu.tutorials.volunteerapp.ui.components.loadCardNumberFromFile
import eu.tutorials.volunteerapp.ui.components.saveCardNumberToFile
import eu.tutorials.volunteerapp.ui.components.saveCvv2ToFile
import eu.tutorials.volunteerapp.ui.theme.Colors
import kotlinx.coroutines.flow.filter

@Composable
fun CardSettings(viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    val userIdValue by viewModel.currentUserId.collectAsState()
    val cards by viewModel.cards.collectAsState()

    var card by remember { mutableStateOf<Card?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cvv2 by remember { mutableStateOf("") }

    var fullCardNumber by remember { mutableStateOf("") }
    var showCardNumber by remember { mutableStateOf(false) }
    var showCvv2 by remember { mutableStateOf(false) }

    var hasError = false
    val cardNumberClean = fullCardNumber.filter { it.isDigit() }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardExpiryError by remember { mutableStateOf<String?>(null) }
    var cvv2Error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userIdValue) {
        if (userIdValue == null) return@LaunchedEffect

        viewModel.loadCards()

        snapshotFlow { cards }
            .filter { it.isNotEmpty() }
            .collect { list ->
                val userCard = cards.find { it.idRecipient == userIdValue }
                card = userCard
                cardNumber = userCard?.number ?: ""
                cardExpiry = userCard?.validityPeriod ?: ""

                Log.e("UserId", "${userIdValue!!}")
                viewModel.loadCvv2(context, userIdValue!!) { loaded ->
                    cvv2 = loaded.toString()
                    isLoading = false
                    Log.e("CVV2", cvv2)
                }

                val loadedNumber = loadCardNumberFromFile(context, userIdValue!!)
                fullCardNumber = loadedNumber?.filter { it.isDigit() } ?: ""
                cardNumber = if (fullCardNumber.length == 16)
                    "*".repeat(12) + fullCardNumber.takeLast(4)
                else
                    fullCardNumber.mapIndexed { i, c -> if (i < fullCardNumber.length - 4) '*' else c }.joinToString("")
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "Картка",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 35.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                OutlinedTextField(
                    value = if (showCardNumber) fullCardNumber else cardNumber,
                    onValueChange = { input ->
                        fullCardNumber = input.filter { it.isDigit() }.take(16)
                        cardNumber = if (!showCardNumber)
                            "*".repeat((fullCardNumber.length - 4).coerceAtLeast(0)) + fullCardNumber.takeLast(4)
                        else fullCardNumber
                    },
                    label = { Text("Номер картки", fontSize = 14.sp) },
                    textStyle = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        IconButton(onClick = { showCardNumber = !showCardNumber }) {
                            Icon(
                                imageVector = if (showCardNumber) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )
                cardNumberError?.let {
                    androidx.compose.material3.Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Термін дії та CVV2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    OutlinedTextField(
                        value = cardExpiry,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '-' }
                            cardExpiry = if (filtered.length > 7) filtered.take(7) else filtered
                        },
                        label = { Text("Термін дії (YYYY-MM)", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.width(150.dp).height(60.dp),
                        singleLine = true
                    )
                    cardExpiryError?.let {
                        androidx.compose.material3.Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }

                Column {
                    OutlinedTextField(
                        value = if (showCvv2) cvv2 else "*".repeat(cvv2.length),
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(3)
                            cvv2 = clean
                            cvv2Error = null
                        },
                        label = { Text("CVV2", fontSize = 14.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.width(150.dp).height(60.dp),
                        singleLine = true,
                        isError = cvv2Error != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { showCvv2 = !showCvv2 }) {
                                Icon(
                                    imageVector = if (showCvv2)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = "Показати/приховати CVV2"
                                )
                            }
                        }
                    )
                    cvv2Error?.let {
                        androidx.compose.material3.Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    hasError = false
                    cardNumberError = null
                    cardExpiryError = null
                    cvv2Error = null

                    if (!cardNumberClean.matches(Regex("^\\d{16}$"))) {
                        cardNumberError = "Номер картки має містити рівно 16 цифр"; hasError = true
                    }

                    if (!cardExpiry.matches(Regex("^\\d{4}-\\d{2}$"))) {
                        cardExpiryError = "Некоректний формат"
                        hasError = true
                    } else {
                        val (year, month) = cardExpiry.split("-").map { it.toIntOrNull() ?: 0 }
                        if (month !in 1..12) {
                            cardExpiryError = "Некоректний місяць"; hasError = true
                        }
                    }

                    if (!cvv2.matches(Regex("^\\d{3}$"))) {
                        cvv2Error = "CVV2 має містити 3 цифри"; hasError = true
                    }

                    if (hasError) return@Button

                    card?.let { c ->

                        val updatedCard = c.copy(
                            number = cardNumber,
                            validityPeriod = cardExpiry
                        )

                        Log.d("CardSettings", "Updating card: $updatedCard")
                        viewModel.updateCard(
                            card = updatedCard,
                        ) { success ->
                            Log.d("CardSettings", "Update result: $success")
                            if (success) {
                                card = updatedCard
                                cardNumber = updatedCard.number
                                cardExpiry = updatedCard.validityPeriod

                                if (cvv2.isNotBlank()) {
                                    saveCvv2ToFile(context, updatedCard.idRecipient, cvv2)
                                    Log.d("CVV2", "CVV2 saved to file for user ${updatedCard.idRecipient}")
                                }

                                if (cardNumber.isNotBlank()) {
                                    saveCardNumberToFile(context, updatedCard.idRecipient, cardNumber)
                                    Log.d("CardNumber", "CardNumber saved to file for user ${updatedCard.idRecipient}")
                                }

                                navController.popBackStack()
                            }
                        }
                    }
                    Toast.makeText(context, "Карту успішно змінено!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(Color(Colors.MainBlue.rgb))
            ) {
                Text("Зберегти", color = Color.White, fontSize = 18.sp)
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