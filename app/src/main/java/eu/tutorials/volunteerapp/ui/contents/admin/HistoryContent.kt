package eu.tutorials.volunteerapp.ui.contents.admin

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import eu.tutorials.volunteerapp.ui.components.prepareHelpHistoryList
import eu.tutorials.volunteerapp.ui.theme.Colors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {
    // Завантаження даних
    LaunchedEffect(userId) {
        viewModel.getAllHelps()
        viewModel.getAllHistories()
        viewModel.loadUsers()
    }

    val helpsList by viewModel.helpViewModel.helps.collectAsState(initial = emptyList())
    val histories by viewModel.userHistories.collectAsState(initial = emptyList())
    val users by viewModel.users.collectAsState(initial = emptyList())
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val isLoading = helpsList.isEmpty() || histories.isEmpty() || users.isEmpty()

    // Для дебагу
    LaunchedEffect(helpsList, histories, users) {
        Log.d("DEBUG", "Helps: $helpsList")
        Log.d("DEBUG", "Histories: $histories")
        Log.d("DEBUG", "Users: $users")
    }

    val filteredHelpWithHistoryList = prepareHelpHistoryList(helpsList, histories)
        .filter { item ->
            val user = users.find { it.id == item.help.idRecipient }

            if (user == null) {
                Log.d(
                    "DEBUG",
                    "History ${item.history.id} пропущено: користувач не знайдений (idRecipient=${item.help.idRecipient})"
                )
                true // щоб картка відобразилася навіть без користувача
            } else if (searchQuery.isBlank()) {
                Log.d("DEBUG", "History ${item.history.id} включено: пошуковий запит пустий")
                true
            } else {
                val query = searchQuery.trim().lowercase()
                val fullName = "${user.lastName} ${user.firstName}".lowercase()
                val matches = fullName.contains(query)
                Log.d(
                    "DEBUG",
                    "History ${item.history.id}: пошуковий запит='$query', fullName='$fullName', matches=$matches"
                )
                matches
            }
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
                "Історія",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(Colors.DarkBlue.rgb)
                )
            )
            Spacer(Modifier.height(20.dp))
        }

        // Пошук
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Пошук",
                    tint = Color(Colors.DarkBlue.rgb),
                    modifier = Modifier.padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Прізвище Ім’я") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(Colors.MainBlue.rgb),
                        unfocusedBorderColor = Color(Colors.MainBlue.rgb),
                        cursorColor = Color(Colors.DarkBlue.rgb),
                        focusedLabelColor = Color(Colors.DarkBlue.rgb)
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
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
        } else if (filteredHelpWithHistoryList.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Запитів не знайдено",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = Color(Colors.DarkBlue.rgb)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredHelpWithHistoryList) { item ->
                HelpCard(
                    help = item.help,
                    history = item.history,
                    viewModel = viewModel,
                    onDetailsClick = {
                        navController.navigate("help_details_for_admin/${item.help.id}")
                    }
                )
            }
        }
    }
}