package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.UserRole
import eu.tutorials.volunteerapp.api.BinAPI
import eu.tutorials.volunteerapp.api.CardAPI
import eu.tutorials.volunteerapp.api.DonationAPI
import eu.tutorials.volunteerapp.api.HelpAPI
import eu.tutorials.volunteerapp.api.HistoryAPI
import eu.tutorials.volunteerapp.api.MaterialParticipationAPI
import eu.tutorials.volunteerapp.api.UserAPI
import eu.tutorials.volunteerapp.data.AuthRequest
import eu.tutorials.volunteerapp.data.AuthResponse
import eu.tutorials.volunteerapp.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private var apiClient: UserAPI = UserAPI()
    private val cardAPI = CardAPI()
    private val helpAPI = HelpAPI()
    private val materialParticipationAPI = MaterialParticipationAPI()
    private val historyAPI = HistoryAPI()
    private val binAPI = BinAPI()
    private val donationAPI = DonationAPI()

    fun getToken(): String? = apiClient.getAuthHeader()

    private val _error = MutableStateFlow<String?>(null)

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _auth = MutableStateFlow<AuthResponse?>(null)


    fun getAll() {
        viewModelScope.launch {
            try {
                _users.value = apiClient.getAllUsers()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun authenticate(
        email: String,
        password: String,
        onResult: (success: Boolean, role: UserRole?) -> Unit
    ) {
        val authRequest = AuthRequest(email, password)
        viewModelScope.launch {
            try {
                val response = apiClient.authenticate(authRequest)
                _auth.value = response

                Log.d("UserViewModel", "Auth token: ${apiClient.getAuthHeader()}")

                val role = when (response.role) {
                    "Admin" -> UserRole.Admin
                    "Recipient" -> UserRole.Recipient
                    "Volunteer" -> UserRole.Volunteer
                    else -> null
                }

                val token = apiClient.getAuthHeader()
                token?.let {
                    cardAPI.updateAuthHeader(it)
                    helpAPI.updateAuthHeader(it)
                    materialParticipationAPI.updateAuthHeader(it)
                    historyAPI.updateAuthHeader(it)
                    donationAPI.updateAuthHeader(it)
                    binAPI.updateAuthHeader(it)
                }

                onResult(response.success, role)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(false, null)
            }
        }
    }

    fun create(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        telegram: String?,
        password: String,
        hasCard: Boolean,
        onResult: (Int?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val role = if (hasCard) UserRole.Recipient else UserRole.Volunteer
                val user = User(firstName, lastName, email, phone, role, telegram, password)
                val createdUser = apiClient.createUser(user)

                getAll()
                onResult(createdUser?.id)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(null)
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            try {
                val deleted = apiClient.deleteUser(id)
                if (deleted) getAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getById(id: Int, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = apiClient.getUserById(id)
                onResult(user)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(null)
            }
        }
    }

    fun findUserByEmail(email: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = apiClient.findUserByEmail(email)
                onResult(user)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(null)
            }
        }
    }

    fun updateUser(user: User, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val updated = apiClient.updateUser(user)
                if (updated) getAll()
                onResult?.invoke(updated)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }

    fun updateUserBlock(userId: Int, isBlocked: Boolean, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val success = apiClient.updateUserBlock(userId, isBlocked)
                if (success) getAll()
                onResult?.invoke(success)
            } catch (e: Exception) {
                _error.value = e.message
                onResult?.invoke(false)
            }
        }
    }
}