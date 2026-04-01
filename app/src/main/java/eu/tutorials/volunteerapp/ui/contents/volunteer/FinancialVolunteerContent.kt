package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.components.DateRangePicker
import eu.tutorials.volunteerapp.ui.components.outlinedField
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FinancialVolunteerContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {

    LaunchedEffect(userId) {
        viewModel.getAllHelps()
        viewModel.getAllHistories()
        viewModel.loadUsers()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    var showFilterDialog by remember { mutableStateOf(false) }

    var titleFilter by remember { mutableStateOf("") }
    var descriptionFilter by remember { mutableStateOf("") }
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }

    var amountFrom by remember { mutableStateOf("") }
    var amountTo by remember { mutableStateOf("") }

    val amountFromValue = amountFrom.toDoubleOrNull()
    val amountToValue = amountTo.toDoubleOrNull()

    val helpsList by viewModel.helpViewModel.helps.collectAsState(initial = emptyList())
    val histories by viewModel.userHistories.collectAsState(initial = emptyList())
    val users by viewModel.users.collectAsState(initial = emptyList())

    val financialHelps = helpsList.filter { it is Help.Financial }

    val helpsWithLastHistory = financialHelps.map { help ->
        val historiesForHelp = histories.filter { it.idHelp == help.id }
        val lastHistory = historiesForHelp.maxByOrNull { it.id ?: 0 }
        help to lastHistory
    }

    val today = LocalDate.now()

    val activeHelpsWithHistory = helpsWithLastHistory.filter { (help, _) ->

        val isApproved = histories.any {
            it.idHelp == help.id && it.status == HistoryStatus.Approved
        }

        val financial = help as? Help.Financial ?: return@filter false

        val isTimeValid = !financial.to.isBefore(today)

        val isNotFullyCollected =
            (financial.collected ?: 0.0) < financial.plannedAmount

        isApproved && isTimeValid && isNotFullyCollected
    }

    val filteredHelps = activeHelpsWithHistory.filter { (help, _) ->

        val user = users.find { it.id == help.idRecipient }
        val query = searchQuery.trim().lowercase()

        val helpDate = help.createdAt.toLocalDate()

        val matchesSearch =
            query.isBlank() ||
                    user?.let {
                        val fullName = "${it.lastName} ${it.firstName}".lowercase()
                        fullName.contains(query)
                    } ?: true

        val matchesTitle =
            titleFilter.isBlank() ||
                    help.title.lowercase().contains(titleFilter.lowercase())

        val matchesDescription =
            descriptionFilter.isBlank() ||
                    help.description.lowercase().contains(descriptionFilter.lowercase())

        val matchesAmount =
            help !is Help.Financial ||
                    (
                            (amountFromValue == null || help.plannedAmount >= amountFromValue) &&
                                    (amountToValue == null || help.plannedAmount <= amountToValue)
                            )

        val matchesFromDate =
            fromDate == null || !helpDate.isBefore(fromDate)

        val matchesToDate =
            toDate == null || !helpDate.isAfter(toDate)

        matchesSearch &&
                matchesTitle &&
                matchesDescription &&
                matchesAmount &&
                matchesFromDate &&
                matchesToDate
    }

    val isLoading = helpsList.isEmpty()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Фінансова допомога",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(Colors.DarkBlue.rgb)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
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
                        contentDescription = "Search",
                        tint = Color(Colors.DarkBlue.rgb),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Прізвище Ім’я") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(Colors.MainBlue.rgb),
                            unfocusedBorderColor = Color(Colors.MainBlue.rgb),
                            cursorColor = Color(Colors.DarkBlue.rgb)
                        )
                    )

                    IconButton(
                        onClick = { showFilterDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color(Colors.DarkBlue.rgb)
                        )
                    }
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
            }

            else if (filteredHelps.isEmpty()) {

                item {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Активних запитів на допомогу немає",
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

                items(filteredHelps) { (help, _) ->

                    HelpCard(
                        help = help,
                        viewModel = viewModel,
                        onDetailsClick = {
                            navController.navigate("financial_help_details_for_volunteer/${help.id}")
                        }
                    )
                }
            }
        }

        if (showFilterDialog) {

            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                containerColor = Color.White,

                title = {

                    Text(
                        "Фільтрація",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(Colors.DarkBlue.rgb)
                    )
                },

                text = {

                    Column {

                        outlinedField(
                            value = titleFilter,
                            onValueChange = { titleFilter = it },
                            label = "Назва",
                            width = 300.dp
                        )

                        outlinedField(
                            value = descriptionFilter,
                            onValueChange = { descriptionFilter = it },
                            label = "Опис",
                            width = 300.dp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DateRangePicker(
                            fromDate = fromDate,
                            toDate = toDate,
                            onFromDateChange = { fromDate = it },
                            onToDateChange = { toDate = it }
                        )

                        TextButton(
                            onClick = {
                                fromDate = null
                                toDate = null
                            }
                        ) {
                            Text("Очистити дати")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            OutlinedTextField(
                                value = amountFrom,
                                onValueChange = {
                                    amountFrom = it.filter { c ->
                                        c.isDigit() || c == '.'
                                    }
                                },
                                label = { Text("Від") },
                                modifier = Modifier.width(110.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(Colors.LightBlue.rgb),
                                    cursorColor = Color(Colors.MainBlue.rgb)
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = amountTo,
                                onValueChange = {
                                    amountTo = it.filter { c ->
                                        c.isDigit() || c == '.'
                                    }
                                },
                                label = { Text("До") },
                                modifier = Modifier.width(110.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(Colors.LightBlue.rgb),
                                    cursorColor = Color(Colors.MainBlue.rgb)
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "грн",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(Colors.DarkBlue.rgb)
                            )
                        }

                        TextButton(
                            onClick = {
                                titleFilter = ""
                                descriptionFilter = ""
                                amountFrom = ""
                                amountTo = ""
                                fromDate = null
                                toDate = null
                            }
                        ) {
                            Text("Очистити фільтр")
                        }
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {

                            showFilterDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(Colors.MainBlue.rgb)
                        )
                    ) {
                        Text("Застосувати")
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = { showFilterDialog = false }
                    ) {
                        Text("Скасувати", color = Color(Colors.DarkBlue.rgb))
                    }
                }
            )
        }
    }
}