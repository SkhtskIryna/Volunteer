package eu.tutorials.volunteerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.Bin
import eu.tutorials.volunteerapp.api.BinAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BinViewModel : ViewModel() {

    private val api = BinAPI()

    private val _bin = MutableStateFlow<List<Bin>>(emptyList())
    val bin = _bin

    private val _error = MutableStateFlow<String?>(null)

    fun getAll(token: String) {
        viewModelScope.launch {
            val result = api.getAllBin(token)
            if (result != null) _bin.value = result
        }
    }

    fun create(bin: Bin, token: String, onResult: (Bin?) -> Unit) {
        viewModelScope.launch {
            val result = api.createBin(bin, token)
            if (result != null) {
                _bin.value = _bin.value + result
            }
            onResult(result)
        }
    }

    fun delete(id: Int, token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = api.deleteBin(id, token)
            if (success) {
                _bin.value = _bin.value.filterNot { it.id == id }
            }
            onResult(success)
        }
    }
}