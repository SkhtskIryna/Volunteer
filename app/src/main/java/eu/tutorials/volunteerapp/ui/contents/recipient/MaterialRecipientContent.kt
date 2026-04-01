package eu.tutorials.volunteerapp.ui.contents.recipient

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help.Material
import eu.tutorials.domain.model.MaterialCategory
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.loadPlacesFromCsv
import eu.tutorials.volunteerapp.ui.components.outlinedField
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaterialRecipientContent(viewModel: MainViewModel, navController: NavController, userId: Int) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Завантаження даних користувача
    LaunchedEffect(userId) {
        viewModel.getUserById(userId) {
            user = it
            isLoading = false
        }
    }

    // Категорії
    val categories = MaterialCategory.values()
    val selectedCategories = remember { mutableStateListOf<MaterialCategory>() }

    val places = remember { loadPlacesFromCsv(context) }

    val regions = places
        .map { it.region }
        .distinct()
        .filter { it.isNotBlank() }
        .filter { it.endsWith("область") }

    var selectedRegion by remember { mutableStateOf<String?>(null) }

    val districts = places
        .filter { it.region == selectedRegion }
        .map { it.rayon }
        .distinct()
        .filter { it.isNotBlank() }

    var selectedDistrict by remember { mutableStateOf<String?>(null) }

    val cities = places
        .filter { it.place == "city" }
        .map { it.name }
        .distinct()
        .filter { it.isNotBlank() }

    var selectedCity by remember { mutableStateOf<String?>(null) }

    val emptyOption = ""

    val regionsWithEmpty = listOf(emptyOption) + regions
    val districtsWithEmpty = listOf(emptyOption) + districts
    val citiesWithEmpty = listOf(emptyOption) + cities

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var categoriesError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = this.maxWidth

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Матеріальна допомога",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp,
                        color = Color(Colors.DarkBlue.rgb),
                        fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Назва",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color(Colors.DarkBlue.rgb),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.padding(4.dp).align(Alignment.Start)
                    )
                    Column {
                        outlinedField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Назва",
                            width = maxWidth
                        )
                        titleError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Опис",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color(Colors.DarkBlue.rgb),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.padding(4.dp).align(Alignment.Start)
                    )
                    Column {
                        outlinedField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Опис",
                            width = maxWidth
                        )
                        descriptionError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Категорії",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color(Colors.DarkBlue.rgb),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.padding(10.dp).align(Alignment.Start)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ){
                        categories.forEach { category ->
                            val isSelected = selectedCategories.contains(category)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(Colors.DarkBlue.rgb) else Color.Gray,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        if (isSelected) selectedCategories.remove(category)
                                        else selectedCategories.add(category)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = category.displayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected)
                                        Color(Colors.DarkBlue.rgb)
                                    else
                                        Color.Gray
                                )
                            }
                        }
                        categoriesError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Column {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (selectedCity.isNullOrEmpty()) {
                            Text(
                                text = "Область",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    color = Color(Colors.DarkBlue.rgb),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(4.dp).align(Alignment.Start)
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
                    }


                    if (!selectedRegion.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Район",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    color = Color(Colors.DarkBlue.rgb),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(4.dp).align(Alignment.Start)
                            )

                            DropdownMenuBox(
                                items = districtsWithEmpty,
                                selectedItem = selectedDistrict,
                                onItemSelected = {
                                    selectedDistrict =
                                        if (it.isEmpty()) null else it; selectedCity = null
                                },
                                label = { if (it.isEmpty()) "Не обирати" else it }
                            )
                        }
                    }

                    if (selectedRegion.isNullOrEmpty() && selectedDistrict.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Місто",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    color = Color(Colors.DarkBlue.rgb),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(4.dp).align(Alignment.Start)
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
                    locationError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Button(
                    onClick = {
                        titleError = null
                        descriptionError = null
                        categoriesError = null
                        locationError = null

                        val isLocationValid =
                            (!selectedCity.isNullOrEmpty()) ||
                                    (!selectedRegion.isNullOrEmpty() && !selectedDistrict.isNullOrEmpty())

                        var hasError = false

                        if (title.isBlank()) {
                            titleError = "Введіть назву"
                            hasError = true
                        }
                        if (description.isBlank()) {
                            descriptionError = "Введіть опис"
                            hasError = true
                        }
                        if (selectedCategories.isEmpty()) {
                            categoriesError = "Оберіть хоча б одну категорію"
                            hasError = true
                        }
                        if (!isLocationValid) {
                            locationError = "Оберіть місто або область та район"
                            hasError = true
                        }

                        if (!hasError) {
                            val help = Material(
                                title = title,
                                description = description,
                                idRecipient = userId,
                                category = selectedCategories.joinToString(",") { it.name },
                                region = selectedRegion ?: "",
                                area = selectedDistrict ?: "",
                                city = selectedCity ?: "",
                                createdAt = LocalDateTime.now()
                            )

                            viewModel.createHelp(help) { createdHelp ->
                                if (createdHelp != null) {
                                    Toast.makeText(
                                        context,
                                        "Допомогу успішно створено!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    title = ""
                                    description = ""
                                    selectedCategories.clear()
                                    selectedRegion = null
                                    selectedDistrict = null
                                    selectedCity = null
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Не вдалося створити допомогу",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(Colors.MainBlue.rgb),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Text(
                        text = "Створити",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun <T> DropdownMenuBox(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    label: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    val blue = Color(Colors.DarkBlue.rgb)

    Column {
        OutlinedTextField(
            value = selectedItem?.let(label) ?: "",
            onValueChange = {},
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blue,
                unfocusedBorderColor = blue,
                cursorColor = blue,
                focusedTrailingIconColor = blue,
                unfocusedTrailingIconColor = blue
            ),
            trailingIcon = {
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.ArrowDropUp
                    else
                        Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            label(item),
                            color = blue
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

