package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.domain.model.Help
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.UserAvatar
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HelpCard(
    help: Help,
    viewModel: MainViewModel,
    onDetailsClick: () -> Unit
) {
   var recipient by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(help.idRecipient) {
        viewModel.getUserById(help.idRecipient) {
            recipient = it
        }
    }

    LaunchedEffect(help.id) {
        val id = help.id ?: return@LaunchedEffect
        viewModel.ensureHistoryForHelp(help)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F8FF)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // LEFT CONTENT
            Column(modifier = Modifier.weight(1f)) {

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

                Spacer(Modifier.height(8.dp))

                Text(
                    text = help.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = Color(Colors.DarkBlue.rgb)
                )

                Spacer(Modifier.height(6.dp))

                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

                Text(
                    text = "Створено: ${help.createdAt.format(formatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(Colors.DarkBlue.rgb)
                )

                help.updatedAt?.let {
                    Text(
                        text = "Оновлено: ${it.format(formatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(Colors.DarkBlue.rgb)
                    )
                }

                Spacer(Modifier.height(8.dp))

                when (help) {
                    is Help.Material -> {
                        val location = listOfNotNull(
                            help.region?.takeIf { it.isNotBlank() },
                            help.area?.takeIf { it.isNotBlank() }
                                ?: help.city?.takeIf { it.isNotBlank() }
                        ).joinToString(", ")

                        if (location.isNotBlank()) {
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(Colors.DarkBlue.rgb)
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "#${help.category}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic
                            ),
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }

                    is Help.Financial -> {
                        Text(
                            text = "Період: ${help.from} – ${help.to}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Планова сума: ${help.plannedAmount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(Colors.DarkBlue.rgb)
                        )
                    }
                }
            }

            // RIGHT ACTIONS
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Деталі",
                    modifier = Modifier.clickable { onDetailsClick() },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = Color(Colors.DarkBlue.rgb)
                    )
                )
            }
        }
    }
}
