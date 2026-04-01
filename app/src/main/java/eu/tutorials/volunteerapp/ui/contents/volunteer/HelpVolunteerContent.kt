package eu.tutorials.volunteerapp.ui.contents.volunteer

import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val histories by viewModel.userHistories.collectAsState()
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
            ).distinct()

    val volunteerHelps by remember(helps, helpIds) {
        derivedStateOf {
            helps.filter { help ->
                help.id != null && helpIds.contains(help.id)
            }
        }
    }

    val helpsWithLastHistory by remember(volunteerHelps, histories) {
        derivedStateOf {

            volunteerHelps.map { help ->

                val lastHistory =
                    histories
                        .filter { it.idRequest == help.id }
                        .maxByOrNull { it.id ?: 0 }

                help to lastHistory
            }
        }
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

            helpsWithLastHistory.isEmpty() -> {

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

                items(helpsWithLastHistory) { (help, _) ->

                    when (help) {
                        is Help.Material -> {
                            HelpCard(
                                help = help,
                                viewModel = viewModel,
                                onDetailsClick = {
                                    navController.navigate(
                                        "material_details_for_volunteer/${help.id}"
                                    )
                                }
                            )
                        }

                        is Help.Financial -> {

                            val donationForHelp = donations.find {
                                it.idFinancialRequest == help.id && it.idVolunteer == userId
                            }

                            HelpCard(
                                help = help,
                                viewModel = viewModel,
                                onDetailsClick = {
                                    navController.navigate(
                                        "financial_details_for_volunteer/${help.id}/${donationForHelp?.id ?: -1}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}