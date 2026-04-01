package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.volunteerapp.api.MaterialParticipationAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaterialParticipationViewModel: ViewModel() {
    private val apiClient = MaterialParticipationAPI()
    private var authToken: String? = null

    private val _error = MutableStateFlow<String?>(null)
    private val _materialParticipation = MutableStateFlow<List<MaterialParticipation>>(emptyList())
    val materialParticipation: StateFlow<List<MaterialParticipation>> = _materialParticipation

    val authMaterialParticipation = mutableStateListOf<MaterialParticipation>()

    fun updateAuthHeader(token: String) {
        authToken = token
        apiClient.updateAuthHeader(token)
        Log.d("MaterialParticipationViewModel", "Auth token updated: $token")
    }

    fun getAll() {
        viewModelScope.launch {
            try {
                _materialParticipation.value = apiClient.getAllMaterialParticipation()!!
                Log.d("MaterialParticipationViewModel", "Get all: ${_materialParticipation.value}")
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getById(id: Int?, onResult: (MaterialParticipation?) -> Unit) {
        viewModelScope.launch {
            try {
                val materialParticipation = apiClient.getMaterialParticipationById(id!!)
                onResult(materialParticipation)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(null)
            }
        }
    }

    fun create(materialParticipation: MaterialParticipation, onResult: (MaterialParticipation?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("MaterialParticipationViewModel", "Creating materialParticipation: $materialParticipation")
                authMaterialParticipation.add(materialParticipation)
                val createdMaterialParticipation = apiClient.createMaterialParticipation(materialParticipation)
                createdMaterialParticipation?.let {
                    _materialParticipation.value = _materialParticipation.value + it
                }
                onResult(createdMaterialParticipation)

                getAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun update(materialParticipation: MaterialParticipation, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val updated = apiClient.updateMaterialParticipation(materialParticipation)
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
                val deleted = apiClient.deleteMaterialParticipation(id)
                if (deleted) {
                    _materialParticipation.value = _materialParticipation.value.filter { it.id != id }
                    authMaterialParticipation.removeIf { it.id == id }
                }
                onResult?.invoke(deleted)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }
}