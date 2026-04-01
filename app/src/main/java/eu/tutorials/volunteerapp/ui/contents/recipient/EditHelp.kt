package eu.tutorials.volunteerapp.ui.contents.recipient

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.MaterialCategory
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.components.DateRangePicker
import eu.tutorials.volunteerapp.ui.components.loadPlacesFromCsv
import eu.tutorials.volunteerapp.ui.theme.Colors
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditHelp(
    helpId: Int,
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val help by viewModel.getHelpByIdFlow(helpId).collectAsState(initial = null)

    // Локальні стани
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categoriesSelected = remember { mutableStateListOf<String>() }
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }
    var plannedAmount by remember { mutableStateOf(0.0) }

    val categories = MaterialCategory.values()
    val selectedCategories = remember { mutableStateListOf<MaterialCategory>() }

    // Помилки
    var dateError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
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


    help?.let { h ->
        LaunchedEffect(h) {
            title = h.title
            description = h.description
            when (h) {
                is Help.Material -> {
                    selectedRegion = h.region?.ifBlank { null }
                    selectedDistrict = h.area?.ifBlank { null }
                    selectedCity = h.city?.ifBlank { null }
                    selectedCategories.clear()
                    h.category.split(",")
                        .mapNotNull { str -> MaterialCategory.values().find { it.name == str } }
                        .forEach { selectedCategories.add(it) }
                }
                is Help.Financial -> {
                    fromDate = h.from
                    toDate = h.to
                    plannedAmount = h.plannedAmount
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item{
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color(Colors.DarkBlue.rgb)
                    )
                }

                Spacer(Modifier.height(8.dp))
            }

            // Назва
            item {
                Text("Назва", style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold
                ))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth()
                )
                titleError?.let { Text(it, color = Color.Red) }
            }

            // Опис
            item {
                Spacer(Modifier.height(8.dp))
                Text("Опис", style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold
                ))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                descriptionError?.let { Text(it, color = Color.Red) }
            }

            item {
                if (h is Help.Material) {
                    Spacer(Modifier.height(8.dp))
                    // Категорії
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
                                        color = if (isSelected) Color(Colors.DarkBlue.rgb) else Color.Gray
                                    )
                                }
                            }
                            categoriesError?.let { Text(text = it, color = Color.Red, fontSize = 14.sp) }
                        }
                    }

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
            }

            if (h is Help.Financial) {
                item {
                    Spacer(Modifier.height(8.dp))
                    DateRangePicker(fromDate, toDate, onFromDateChange = { fromDate = it }, onToDateChange = { toDate = it })
                    dateError?.let { Text(it, color = Color.Red) }

                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField( value = plannedAmount.toString(), onValueChange = { plannedAmount = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Планова сума") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) )
                    amountError?.let { Text(it, color = Color.Red) }
                }
            }

            // Збереження
            item {
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    // Скидання помилок
                    titleError = null
                    descriptionError = null
                    locationError = null
                    categoriesError = null
                    dateError = null
                    amountError = null

                    var hasError = false
                    if (title.isBlank()) { titleError = "Введіть назву"; hasError = true }
                    if (description.isBlank()) { descriptionError = "Введіть опис"; hasError = true }

                    when (h) {
                        is Help.Material -> {
                            if (selectedCategories.isEmpty()) {
                                categoriesError = "Оберіть хоча б одну категорію"
                                hasError = true
                            }
                            if (selectedRegion.isNullOrEmpty() && selectedDistrict.isNullOrEmpty() && selectedCity.isNullOrEmpty()) {
                                locationError = "Оберіть місцезнаходження"
                                hasError = true
                            }
                        }
                        is Help.Financial -> {
                            if (fromDate == null || toDate == null) { dateError = "Введіть дати"; hasError = true }
                            val amount = plannedAmount
                            if (amount == null) { amountError = "Введіть коректну суму"; hasError = true }
                            else if (amount > 20000) { amountError = "Сума не більше 20 000"; hasError = true }
                        }
                    }

                    if (!hasError) {
                        val updatedHelp = when (h) {
                            is Help.Material -> h.copy(
                                title = title,
                                description = description,
                                region = selectedRegion,
                                area = selectedDistrict,
                                city = selectedCity,
                                category = selectedCategories.joinToString(",") { it.name }
                            )
                            is Help.Financial -> h.copy(
                                title = title,
                                description = description,
                                from = fromDate ?: LocalDate.now(),
                                to = toDate ?: LocalDate.now().plusDays(30),
                                plannedAmount = plannedAmount ?: 0.0
                            )
                        }
                        viewModel.updateHelp(updatedHelp) { success ->
                            if (success) {
                                Toast.makeText(context, "Допомогу успішно змінено!", Toast.LENGTH_LONG).show()

                                // Очистка полів
                                title = ""
                                description = ""
                                selectedRegion = null
                                selectedDistrict = null
                                selectedCity = null
                                categoriesSelected.clear()
                                fromDate = null
                                toDate = null
                                plannedAmount = 0.0

                                // Повернення на попередню сторінку
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Не вдалося змінити допомогу", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors( containerColor = Color(Colors.MainBlue.rgb) )) {
                    Text("Зберегти", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}
