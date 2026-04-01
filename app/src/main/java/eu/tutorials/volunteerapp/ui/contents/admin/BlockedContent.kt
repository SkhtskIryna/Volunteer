package eu.tutorials.volunteerapp.ui.contents.admin

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun BlockedContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {
    LaunchedEffect(userId) {
        viewModel.loadUsers()
        viewModel.loadCards()
    }

    val users by viewModel.recipients.collectAsState(initial = emptyList())
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val cards by viewModel.cards.collectAsState(initial = emptyList())
    val isLoading = users.isEmpty()

    LaunchedEffect(users) {
        Log.d("DEBUG", "Users: $users")
    }

    val filteredUsers by remember(users, searchQuery) {
        derivedStateOf {
            users
                .filter {
                    val fullName = "${it.lastName} ${it.firstName}".lowercase()
                    fullName.contains(searchQuery.lowercase())
                }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Заблокавані користувачі",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(Colors.DarkBlue.rgb)
                )
            )
            Spacer(Modifier.height(20.dp))
        }

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
        } else if (filteredUsers.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Користувачів не знайдено",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = Color(Colors.DarkBlue.rgb)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredUsers) { user ->
                val userCard = viewModel.getCardByUserId(user.id!!)
                BlockedUserListItem(
                    user = user,
                    card = userCard,
                    onToggleBlock = {
                        viewModel.toggleUserBlock(user)
                    }
                )
            }
        }
    }
}