package eu.tutorials.volunteerapp.ui.contents.admin

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.HistoryStatus
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
    history: History?,
    viewModel: MainViewModel,
    onDetailsClick: () -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

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

    val dismissState = rememberDismissState(
        confirmStateChange = { value ->
            when (value) {
                DismissValue.DismissedToStart -> {
                    showRejectDialog = true
                    false
                }
                DismissValue.DismissedToEnd -> {
                    showApproveDialog = true
                    false
                }
                else -> false
            }
        }
    )

    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Схвалення запиту", color = Color(Colors.DarkBlue.rgb)) },
            text = { Text("Ви впевнені, що бажаєте схвалити запит?") },
            confirmButton = {
                TextButton(onClick = {
                    val adminId = viewModel.currentUserId.value ?: return@TextButton
                    viewModel.createHistory(
                        history = History(
                            status = HistoryStatus.Approved,
                            idAdmin = adminId,
                            idRequest = help.id!!
                        )
                    ){}
                    showApproveDialog = false
                }) {
                    Text("Так")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Ні")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Відхилення запиту") },
            text = { Text("Ви впевнені, що бажаєте відхилити запит?") },
            confirmButton = {
                TextButton(onClick = {
                    val adminId = viewModel.currentUserId.value ?: return@TextButton
                    viewModel.createHistory(
                        history = History(
                            status = HistoryStatus.Rejected,
                            idAdmin = adminId,
                            idRequest = help.id!!
                        )
                    ){}
                    showRejectDialog = false
                }) {
                    Text("Так")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Ні")
                }
            }
        )
    }



    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart
        ),
        background = {
            val direction = dismissState.dismissDirection

            when (direction) {
                DismissDirection.StartToEnd -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .offset(y = 20.dp)
                            .background(Color(0xFF8DE9AC)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Схвалити", color = Color.White)
                        }
                    }
                }

                DismissDirection.EndToStart -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .offset(y = 20.dp)
                            .background(Color(0xFFE98D8D)),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Відхилити", color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                else -> {}
            }
        },
        dismissContent = {
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

                            if (recipient != null) {
                                UserAvatar(
                                    photoBase64 = recipient!!.photoBase64,
                                    firstName = recipient!!.firstName,
                                    lastName = recipient!!.lastName
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(Colors.MainBlue.rgb))
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = recipient?.let { "${it.lastName} ${it.firstName}" } ?: "Завантаження...",
                                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(Colors.DarkBlue.rgb)
                            )
                        }

                        Spacer(Modifier.width(50.dp))

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
                                    help.area?.takeIf { it.isNotBlank() } ?: help.city?.takeIf { it.isNotBlank() }
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
                        val lastHistoryForHelp = history

                        if(lastHistoryForHelp?.status == HistoryStatus.Approved){
                            Text(
                                text = "СХВАЛЕНО",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = Color.Green
                            )
                        }
                        else if(lastHistoryForHelp?.status == HistoryStatus.Rejected){
                            Text(
                                text = "ВІДХИЛЕНО",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = Color.Red
                            )
                        }
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
    )
}
