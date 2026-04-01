package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.Donation
import eu.tutorials.volunteerapp.api.DonationAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonationViewModel : ViewModel() {

    private val apiDonation = DonationAPI()

    private val _error = MutableStateFlow<String?>(null)

    private val _donations = MutableStateFlow<List<Donation>>(emptyList())
    val donations: StateFlow<List<Donation>> = _donations

    fun updateAuthHeader(token: String) {
        apiDonation.updateAuthHeader(token)
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                _donations.value = apiDonation.getAllDonations() ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun create(donation: Donation?, onResult: (Donation?) -> Unit) {
        viewModelScope.launch {
            val result = try {
                apiDonation.createDonation(donation!!)
            } catch (e: Exception) {
                Log.e("DonationDebug", "Create donation error: ${e.message}")
                null
            }
            Log.d("DonationDebug", "Donation creation result: $result")
            onResult(result)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            try {
                val deleted = apiDonation.deleteDonation(id)
                if (deleted) getAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}