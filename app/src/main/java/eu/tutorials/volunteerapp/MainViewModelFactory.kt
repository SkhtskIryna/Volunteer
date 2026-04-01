package eu.tutorials.volunteerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eu.tutorials.volunteerapp.viewmodels.BinViewModel
import eu.tutorials.volunteerapp.viewmodels.CardViewModel
import eu.tutorials.volunteerapp.viewmodels.ClientTokenViewModel
import eu.tutorials.volunteerapp.viewmodels.DonationViewModel
import eu.tutorials.volunteerapp.viewmodels.HelpViewModel
import eu.tutorials.volunteerapp.viewmodels.HistoryViewModel
import eu.tutorials.volunteerapp.viewmodels.MaterialParticipationViewModel
import eu.tutorials.volunteerapp.viewmodels.UserViewModel

class MainViewModelFactory(
    private val userViewModel: UserViewModel,
    private val cardViewModel: CardViewModel,
    private val helpViewModel: HelpViewModel,
    private val materialParticipationViewModel: MaterialParticipationViewModel,
    private val historyViewModel: HistoryViewModel,
    private val binViewModel: BinViewModel,
    private val donationViewModel: DonationViewModel,
    private val clientTokenViewModel: ClientTokenViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(userViewModel, cardViewModel, helpViewModel,
                materialParticipationViewModel, historyViewModel, binViewModel,
                donationViewModel, clientTokenViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}