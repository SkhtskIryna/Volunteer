package eu.tutorials.volunteerapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.tutorials.volunteerapp.ui.components.App
import eu.tutorials.volunteerapp.ui.theme.VolunteerAppTheme
import eu.tutorials.volunteerapp.viewmodels.BinViewModel
import eu.tutorials.volunteerapp.viewmodels.CardViewModel
import eu.tutorials.volunteerapp.viewmodels.ClientTokenViewModel
import eu.tutorials.volunteerapp.viewmodels.DonationViewModel
import eu.tutorials.volunteerapp.viewmodels.HelpViewModel
import eu.tutorials.volunteerapp.viewmodels.HistoryViewModel
import eu.tutorials.volunteerapp.viewmodels.MaterialParticipationViewModel
import eu.tutorials.volunteerapp.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userViewModel: UserViewModel = viewModel()
            val cardViewModel: CardViewModel = viewModel()
            val helpViewModel: HelpViewModel = viewModel()
            val materialParticipationViewModel: MaterialParticipationViewModel = viewModel()
            val historyViewModel: HistoryViewModel = viewModel()
            val binViewModel: BinViewModel = viewModel()
            val donationViewModel: DonationViewModel = viewModel()
            val clientTokenViewModel: ClientTokenViewModel = viewModel()
            val viewModel : MainViewModel = viewModel(
                factory = MainViewModelFactory(userViewModel, cardViewModel, helpViewModel,
                    materialParticipationViewModel, historyViewModel, binViewModel,
                    donationViewModel, clientTokenViewModel)
            )
            VolunteerAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    App(viewModel)
                }
            }
        }
    }
}