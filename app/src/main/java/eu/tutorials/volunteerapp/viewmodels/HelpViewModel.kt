package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.Help
import eu.tutorials.volunteerapp.api.HelpAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HelpViewModel: ViewModel() {
    private val apiClient = HelpAPI()
    private var authToken: String? = null

    private val _error = MutableStateFlow<String?>(null)
    private val _helps = MutableStateFlow<List<Help>>(emptyList())
    val helps: StateFlow<List<Help>> = _helps

    val authHelps = mutableStateListOf<Help>()

    fun updateAuthHeader(token: String) {
        authToken = token
        apiClient.updateAuthHeader(token)
        Log.d("HelpViewModel", "Auth token updated: $token")
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                _helps.value = apiClient.getAllHelps()!!
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getById(id: Int, onResult: (Help?) -> Unit) {
        viewModelScope.launch {
            try {
                val help = apiClient.getHelpById(id)
                onResult(help)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(null)
            }
        }
    }

    fun create(help: Help, onResult: (Help?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("HelpViewModel", "Creating help: $help")
                authHelps.add(help)
                val createdHelp = apiClient.createHelp(help)
                createdHelp?.let {
                    _helps.value = _helps.value + it
                }
                onResult(createdHelp)

                getAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun update(help: Help, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val updated = apiClient.updateHelp(help)
                if (updated) getAll()
                onResult?.invoke(updated)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }

    fun delete(id: Int, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val deleted = apiClient.deleteHelp(id)
                if (deleted) {
                    _helps.value = _helps.value.filter { it.id != id }
                    authHelps.removeIf { it.id == id }
                }
                onResult?.invoke(deleted)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }
}