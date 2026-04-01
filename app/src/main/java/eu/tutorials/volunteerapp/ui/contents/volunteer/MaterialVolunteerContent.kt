package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.domain.model.MaterialCategory
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.components.loadPlacesFromCsv
import eu.tutorials.volunteerapp.ui.components.outlinedField
import eu.tutorials.volunteerapp.ui.contents.recipient.DropdownMenuBox
import eu.tutorials.volunteerapp.ui.theme.Colors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialVolunteerContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {
    val context = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.getAllHelps()
        viewModel.getAllHistories()
        viewModel.loadUsers()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Фільтри
    var titleFilter by remember { mutableStateOf("") }
    var descriptionFilter by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(listOf<MaterialCategory>()) }
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    var selectedDistrict by remember { mutableStateOf<String?>(null) }
    var selectedCity by remember { mutableStateOf<String?>(null) }

    val helpsList by viewModel.helpViewModel.helps.collectAsState(initial = emptyList())
    val histories by viewModel.userHistories.collectAsState(initial = emptyList())
    val users by viewModel.users.collectAsState(initial = emptyList())

    val materialHelps = helpsList.filter { it is Help.Material }

    val helpsWithLastHistory = materialHelps.map { help ->
        val historiesForHelp = histories.filter { it.idHelp == help.id }
        val lastHistory = historiesForHelp.maxByOrNull { it.id ?: 0 }
        help to lastHistory
    }

    val activeHelpsWithHistory = helpsWithLastHistory.filter { (help, _) ->
        histories.any { it.idHelp == help.id && it.status == HistoryStatus.Approved }
    }

    val filteredHelps = activeHelpsWithHistory.filter { (help, _) ->

        if (help !is Help.Material) return@filter false  // тільки матеріальна допомога

        val user = users.find { it.id == help.idRecipient }
        val query = searchQuery.trim().lowercase()

        val matchesSearch =
            query.isBlank() || user?.let { "${it.lastName} ${it.firstName}".lowercase().contains(query) } ?: true

        val matchesTitle =
            titleFilter.isBlank() || help.title.lowercase().contains(titleFilter.lowercase())

        val matchesDescription =
            descriptionFilter.isBlank() || help.description.lowercase().contains(descriptionFilter.lowercase())

        val matchesCategory =
            selectedCategories.isEmpty() || selectedCategories.any { help.category.contains(it.name) }

        val matchesRegion =
            selectedRegion.isNullOrEmpty() || help.region == selectedRegion

        val matchesDistrict =
            selectedDistrict.isNullOrEmpty() || help.area == selectedDistrict

        val matchesCity =
            selectedCity.isNullOrEmpty() || help.city == selectedCity

        matchesSearch &&
                matchesTitle &&
                matchesDescription &&
                matchesCategory &&
                matchesRegion &&
                matchesDistrict &&
                matchesCity
    }

    val places = remember { loadPlacesFromCsv(context) }
    val emptyOption = ""

    val regions = places
        .map { it.region }
        .distinct()
        .filter { it.isNotBlank() }
        .filter { it.endsWith("область") }

    val districts = places
        .filter { it.region == selectedRegion }
        .map { it.rayon }
        .distinct()
        .filter { it.isNotBlank() }

    val cities = places
        .filter { it.place == "city" }
        .map { it.name }
        .distinct()
        .filter { it.isNotBlank() }

    val regionsWithEmpty = listOf(emptyOption) + regions
    val districtsWithEmpty = listOf(emptyOption) + districts
    val citiesWithEmpty = listOf(emptyOption) + cities

    val isLoading = helpsList.isEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Матеріальна допомога",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(Colors.DarkBlue.rgb)
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Пошук та фільтр
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
                    IconButton(onClick = { showFilterDialog = true }) {
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
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (filteredHelps.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
            } else {
                itemsIndexed(filteredHelps) { index, pair ->
                    val (help, lastHistory) = pair
                    HelpCard(
                        help = help,
                        viewModel = viewModel,
                        onDetailsClick = { navController.navigate("material_help_details_for_volunteer/${help.id}") }
                    )
                }
            }
        }

        // AlertDialog для фільтрації
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

                        // Вибір категорій
                        Text("Категорії", fontWeight = FontWeight.Bold, color = Color(Colors.DarkBlue.rgb))
                        MaterialCategory.values().forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategories = if (selectedCategories.contains(category))
                                            selectedCategories - category
                                        else selectedCategories + category
                                    }
                                    .padding(4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedCategories.contains(category),
                                    onCheckedChange = null
                                )
                                Text(category.displayName, color = Color(Colors.DarkBlue.rgb))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column {

                            if (selectedCity.isNullOrEmpty()) {

                                Text(
                                    "Область",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(Colors.DarkBlue.rgb)
                                )

                                DropdownMenuBox(
                                    items = regionsWithEmpty,
                                    selectedItem = selectedRegion,
                                    onItemSelected = {
                                        selectedRegion = if (it.isEmpty()) null else it
                                        selectedDistrict = null
                                        selectedCity = null
                                    },
                                    label = { if (it.isEmpty()) "Не обирати" else it }
                                )
                            }

                            if (!selectedRegion.isNullOrEmpty()) {

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    "Район",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(Colors.DarkBlue.rgb)
                                )

                                DropdownMenuBox(
                                    items = districtsWithEmpty,
                                    selectedItem = selectedDistrict,
                                    onItemSelected = {
                                        selectedDistrict = if (it.isEmpty()) null else it
                                        selectedCity = null
                                    },
                                    label = { if (it.isEmpty()) "Не обирати" else it }
                                )
                            }

                            if (selectedRegion.isNullOrEmpty() && selectedDistrict.isNullOrEmpty()) {

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    "Місто",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(Colors.DarkBlue.rgb)
                                )

                                DropdownMenuBox(
                                    items = citiesWithEmpty,
                                    selectedItem = selectedCity,
                                    onItemSelected = {
                                        selectedCity = if (it.isEmpty()) null else it

                                        if (!selectedCity.isNullOrEmpty()) {
                                            selectedRegion = null
                                            selectedDistrict = null
                                        }
                                    },
                                    label = { if (it.isEmpty()) "Не обирати" else it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                titleFilter = ""
                                descriptionFilter = ""
                                selectedCategories = listOf()
                                selectedRegion = null
                                selectedDistrict = null
                                selectedCity = null
                            }
                        ) {
                            Text("Очистити фільтр", color = Color.DarkGray)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFilterDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(Colors.MainBlue.rgb))
                    ) { Text("Застосувати") }
                },
                dismissButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Скасувати", color = Color(Colors.DarkBlue.rgb))
                    }
                }
            )
        }
    }
}