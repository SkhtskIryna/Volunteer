package eu.tutorials.volunteerapp.ui.contents.recipient

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.theme.Colors
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateRequestsContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {
    // Завантаження даних
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadHelpsForUser(userId)
            viewModel.getAllHistories()
            viewModel.loadMaterialParticipationsForRecipient(userId)
        }
    }

    val helpsList by viewModel.helpsList.collectAsState()
    val materialParticipations by viewModel.materialParticipationList.collectAsState()
    val helpsWithLastHistory = helpsList.map { help ->
        val historiesForHelp = viewModel.userHistories.value.filter { it.idRequest == help.id }
        val lastHistory = historiesForHelp.maxByOrNull { it.id ?: 0 }
        help to lastHistory
    }

    val materialParticipationMap = remember(materialParticipations) {
        materialParticipations.associateBy { it.idHelp }
    }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(helpsList, materialParticipations) {
        if (helpsList.isNotEmpty() || materialParticipations.isNotEmpty()) {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Створені запити",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(Colors.DarkBlue.rgb)
                )
            )
            Spacer(Modifier.height(20.dp))
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        else if (helpsList.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "Запитів на допомогу поки що не створено",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = Color(Colors.DarkBlue.rgb)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else {
            items(helpsWithLastHistory) { (help, lastHistory) ->
                HelpCard(
                    help = help,
                    viewModel = viewModel,
                    materialParticipation = materialParticipationMap[help.id],
                    history = lastHistory,
                    onEditClick = { navController.navigate("help_edit/${help.id}") },
                    onDetailsClick = { navController.navigate("help_details/${help.id}") }
                )
            }
        }
    }
}