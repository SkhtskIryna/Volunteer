package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.volunteerapp.api.CardAPI
import eu.tutorials.volunteerapp.data.AuthCard
import eu.tutorials.volunteerapp.data.Card
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class CardViewModel : ViewModel() {

    private val apiClient = CardAPI()

    private val _error = MutableStateFlow<String?>(null)

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards

    val authCards = mutableStateListOf<AuthCard>()

    fun updateAuthHeader(token: String) {
        apiClient.updateAuthHeader(token)
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                _cards.value = apiClient.getAllCards()!!
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun create(number: String, validityPeriod: YearMonth, cvv2: Int, idRecipient: Int) {
        viewModelScope.launch {
            try {
                Log.d("CreateCard", "Starting card creation")
                val card = Card(
                    number = number,
                    validityPeriod = validityPeriod.toString(),
                    idRecipient = idRecipient
                )
                Log.d("CreateCard", "createCard called with card: $card")
                val result = apiClient.createCard(card)
                Log.d("CreateCard", "createCard returned: $result")

                val authCard = AuthCard(
                    number = number,
                    validityPeriod = validityPeriod,
                    cvv2 = cvv2
                )
                authCards.add(authCard)

                getAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateCard(card: Card, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val updated = apiClient.updateCard(card)
                if (updated) getAll()
                onResult?.invoke(updated)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            try {
                val deleted = apiClient.deleteCard(id)
                if (deleted) getAll()
                authCards.removeIf { it.number.endsWith(id.toString()) }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
