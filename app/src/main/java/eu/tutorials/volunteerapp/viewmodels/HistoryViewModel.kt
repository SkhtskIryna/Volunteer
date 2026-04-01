package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.History
import eu.tutorials.volunteerapp.api.HistoryAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val apiClient = HistoryAPI()
    private var authToken: String? = null

    private val _error = MutableStateFlow<String?>(null)
    private val _history = MutableStateFlow<List<History>>(emptyList())
    val history: StateFlow<List<History>> = _history

    val authHistory = mutableStateListOf<History>()

    fun updateAuthHeader(token: String) {
        authToken = token
        apiClient.updateAuthHeader(token)
        Log.d("HistoryViewModel", "Auth token updated: $token")
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                _history.value = apiClient.getAllHistory()!!
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun create(history: History, onResult: (History?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("HistoryViewModel", "Creating history: $history")

                val createdHistory = apiClient.createHistory(history)

                if (createdHistory != null) {
                    // додаємо лише реально створену історію
                    _history.value = _history.value + createdHistory
                    authHistory.add(createdHistory)
                }

                onResult(createdHistory)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(null)
            }
        }
    }

    fun delete(id: Int, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val deleted = apiClient.deleteHistory(id)
                if (deleted) {
                    _history.value = _history.value.filter { it.id != id }
                    authHistory.removeIf { it.id == id }
                }
                onResult?.invoke(deleted)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }
}