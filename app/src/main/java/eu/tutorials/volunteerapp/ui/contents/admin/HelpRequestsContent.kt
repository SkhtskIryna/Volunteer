package eu.tutorials.volunteerapp.ui.contents.admin

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.theme.Colors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HelpRequestsContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {
    LaunchedEffect(userId) {
        viewModel.getAllHelps()
    }

    LaunchedEffect(Unit) {
        viewModel.getAllHistories()
    }

    val helpsList by viewModel.helpViewModel.helps.collectAsState(initial = emptyList())
    val histories by viewModel.userHistories.collectAsState()
    val helpsWithLastHistory = helpsList.map { help ->
        val historiesForHelp = viewModel.userHistories.value.filter { it.idRequest == help.id }
        val lastHistory = historiesForHelp.maxByOrNull { it.id ?: 0 }
        help to lastHistory
    }

    val activeHelpsWithHistory = helpsWithLastHistory.filter { (help, lastHistory) ->
        histories.none { history ->
            history.idHelp == help.id &&
                    history.status in setOf(HistoryStatus.Approved, HistoryStatus.Rejected)
        }
    }

    val isLoading = helpsList.isEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Запити на допомогу",
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
        } else if (activeHelpsWithHistory.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Активних запитів на допомогу немає",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = Color(Colors.DarkBlue.rgb)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(activeHelpsWithHistory) { (help, lastHistory) ->
                HelpCard(
                    help = help,
                    history = lastHistory,
                    viewModel = viewModel,
                    onDetailsClick = {
                        navController.navigate("help_details_for_admin/${help.id}")
                    }
                )
            }
        }
    }
}