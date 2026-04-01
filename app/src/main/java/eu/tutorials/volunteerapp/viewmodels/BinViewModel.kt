package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.Bin
import eu.tutorials.volunteerapp.api.BinAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BinViewModel: ViewModel() {
    private val apiClient = BinAPI()
    private var authToken: String? = null

    private val _error = MutableStateFlow<String?>(null)
    private val _bin = MutableStateFlow<List<Bin>>(emptyList())

    val authBin = mutableStateListOf<Bin>()

    fun updateAuthHeader(token: String) {
        authToken = token
        apiClient.updateAuthHeader(token)
        Log.d("BinViewModel", "Auth token updated: $token")
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                _bin.value = apiClient.getAllBin()!!
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun create(bin: Bin, onResult: (Bin?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("BinViewModel", "Creating bin: $bin")
                authBin.add(bin)
                val createdBin = apiClient.createBin(bin)
                createdBin?.let {
                    _bin.value = _bin.value + it
                }
                onResult(createdBin)

                getAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun delete(id: Int, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val deleted = apiClient.deleteBin(id)
                if (deleted) {
                    _bin.value = _bin.value.filter { it.id != id }
                    authBin.removeIf { it.id == id }
                }
                onResult?.invoke(deleted)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }
}