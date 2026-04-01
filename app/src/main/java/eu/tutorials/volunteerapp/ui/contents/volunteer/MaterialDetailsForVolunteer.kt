package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.MaterialParticipationStatus
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialDetailsForVolunteer(
    helpId: Int,
    viewModel: MainViewModel,
    navController: NavController
) {
    val help by viewModel.getHelpByIdFlow(helpId).collectAsState()
    val materialParticipation by viewModel.getMaterialParticipationByHelpId(helpId).collectAsState(initial = null)

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

                    (h as? Help.Material)?.let { material ->

                        val location = listOfNotNull(
                            material.region?.takeIf { it.isNotBlank() },
                            material.area?.takeIf { it.isNotBlank() }
                                ?: material.city?.takeIf { it.isNotBlank() }
                        ).joinToString(", ")

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
                            text = "#${material.category}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = Color(Colors.DarkBlue.rgb)
                        )

                        Spacer(Modifier.height(12.dp))

                        material.description.takeIf { it.isNotBlank() }?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(Colors.DarkBlue.rgb)
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        when (materialParticipation?.status) {

                            null, MaterialParticipationStatus.Registered -> {
                                Button (
                                    onClick = {
                                        viewModel.updateMaterialStatus(
                                            helpId,
                                            MaterialParticipationStatus.InProgress
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(Colors.MainBlue.rgb),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("В процесі")
                                }
                            }

                            MaterialParticipationStatus.InProgress -> {
                                Button(
                                    onClick = {
                                        viewModel.updateMaterialStatus(
                                            helpId,
                                            MaterialParticipationStatus.Delivered
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(Colors.MainBlue.rgb),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Доставлено")
                                }
                            }

                            MaterialParticipationStatus.Delivered -> {
                                Text(
                                    text = "Допомогу успішно доставлено об ${
                                        materialParticipation?.deliveredAt?.format(formatter)
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 18.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
