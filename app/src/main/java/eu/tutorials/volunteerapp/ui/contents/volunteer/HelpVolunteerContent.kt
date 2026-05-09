package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.os.Build
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.domain.model.Help
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.theme.Colors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HelpVolunteerContent(
    viewModel: MainViewModel,
    navController: NavController,
    userId: Int
) {

    LaunchedEffect(userId) {
        viewModel.getAllMaterialParticipation()
        viewModel.getAllDonation()
        viewModel.getAllHelps()
        viewModel.getAllHistories()
        viewModel.loadUsers()
    }

    val helps by viewModel.helpsList.collectAsState()
    val materialParticipations by viewModel.materialParticipationList.collectAsState()
    val donations by viewModel.donations.collectAsState()

    val isLoading =
        helps.isEmpty() &&
                materialParticipations.isEmpty() &&
                donations.isEmpty()

    val helpIds = (
            donations
                .filter { it.idVolunteer == userId }
                .map { it.idFinancialRequest } +

                    materialParticipations
                        .filter { it.idVolunteer == userId }
                        .map { it.idMaterialRequest }
            )

    val financialItems = donations
        .filter { it.idVolunteer == userId }
        .mapNotNull { donation ->
            val help = helps.find { it.id == donation.idFinancialRequest }
            help?.let { it to donation }
        }

    val materialItems = materialParticipations
        .filter { it.idVolunteer == userId }
        .mapNotNull { participation ->
            val help = helps.find { it.id == participation.idMaterialRequest }
            help?.let { it to participation }
        }

    LaunchedEffect(donations, materialParticipations) {
        Log.d("VOLUNTEER", "donations: $donations")
        Log.d("VOLUNTEER", "materialParticipations: $materialParticipations")
        Log.d("VOLUNTEER", "helpIds recomputed: $helpIds")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            Spacer(Modifier.height(16.dp))

            Text(
                "Надані допомоги",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(Colors.DarkBlue.rgb)
                )
            )

            Spacer(Modifier.height(20.dp))
        }

        when {

            isLoading -> {

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            financialItems.isEmpty() && materialItems.isEmpty() -> {
                item {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ви ще не надали жодної допомоги",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 20.sp,
                                color = Color(Colors.DarkBlue.rgb)
                            )
                        )
                    }
                }
            }

            else -> {
                items(materialItems) { (help, _) ->

                    HelpCard(
                        help = help as Help.Material,
                        viewModel = viewModel,
                        onDetailsClick = {
                            navController.navigate(
                                "material_details_for_volunteer/${help.id}"
                            )
                        }
                    )
                }

                items(financialItems) { (help, donation) ->

                    HelpCard(
                        help = help as Help.Financial,
                        viewModel = viewModel,
                        onDetailsClick = {
                            navController.navigate(
                                "financial_details_for_volunteer/${help.id}/${donation.id}"
                            )
                        }
                    )
                }
            }
        }
    }
}