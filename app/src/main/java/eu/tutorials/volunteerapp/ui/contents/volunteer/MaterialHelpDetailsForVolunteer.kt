package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.domain.model.MaterialParticipationStatus
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.UserAvatar
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialHelpDetailsForVolunteer(
    helpId: Int,
    viewModel: MainViewModel,
    navController: NavController
) {
    val help by viewModel.getHelpByIdFlow(helpId).collectAsState(initial = null)
    var recipient by remember { mutableStateOf<User?>(null) }
    val materialParticipation by viewModel
        .getMaterialParticipationByHelpId(helpId)
        .collectAsState(initial = null)

    val h = help as? Help.Material

    if (h == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(help?.idRecipient) {
        help?.idRecipient?.let { recipientId ->
            viewModel.getUserById(recipientId) { user ->
                recipient = user
            }
        }
    }

    val location = listOfNotNull(
        h.region?.takeIf { it.isNotBlank() },
        h.area?.takeIf { it.isNotBlank() } ?: h.city?.takeIf { it.isNotBlank() }
    ).joinToString(", ")

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

                    if (location.isNotBlank()) {
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Статус: ${materialParticipation?.status?.name ?: "створено"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(Colors.DarkBlue.rgb)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "#${(help as Help.Material).category}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = Color(Colors.DarkBlue.rgb)
                    )

                    Spacer(Modifier.height(12.dp))

                    h.description.takeIf { it.isNotBlank() }?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val volunteerId = viewModel.currentUserId.value
                            viewModel.createMaterialParticipation(
                                materialParticipation = MaterialParticipation(
                                    idVolunteer = volunteerId!!,
                                    status = MaterialParticipationStatus.Registered,
                                    idMaterialRequest = h.id!!
                                )
                            )
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8DE9AC),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(220.dp)
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Допомогти")
                    }
                }
            }
        }
    }
}