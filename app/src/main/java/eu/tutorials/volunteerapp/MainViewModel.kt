package eu.tutorials.volunteerapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.Donation
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.domain.model.MaterialParticipationStatus
import eu.tutorials.domain.model.UserRole
import eu.tutorials.volunteerapp.data.Card
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.components.getCvv2File
import eu.tutorials.volunteerapp.viewmodels.BinViewModel
import eu.tutorials.volunteerapp.viewmodels.CardViewModel
import eu.tutorials.volunteerapp.viewmodels.ClientTokenViewModel
import eu.tutorials.volunteerapp.viewmodels.DonationViewModel
import eu.tutorials.volunteerapp.viewmodels.HelpViewModel
import eu.tutorials.volunteerapp.viewmodels.HistoryViewModel
import eu.tutorials.volunteerapp.viewmodels.MaterialParticipationViewModel
import eu.tutorials.volunteerapp.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.YearMonth

class MainViewModel(val userViewModel: UserViewModel,
                    private val cardViewModel: CardViewModel,
                    internal val helpViewModel: HelpViewModel,
                    private val materialParticipationViewModel: MaterialParticipationViewModel,
                    private val historyViewModel: HistoryViewModel,
                    private val binViewModel: BinViewModel,
                    private val donationViewModel: DonationViewModel,
                    private val clientTokenViewModel: ClientTokenViewModel): ViewModel(){
    val users: StateFlow<List<User>> get() = userViewModel.users
    val cards: StateFlow<List<Card>> get() = cardViewModel.cards

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId.asStateFlow()

    private val _helpsList = MutableStateFlow<List<Help>>(emptyList())
    val helpsList: StateFlow<List<Help>> = _helpsList

    private val _isHelpsLoading = MutableStateFlow(true)

    private val _userHistories = MutableStateFlow<List<History>>(emptyList())
    val userHistories: StateFlow<List<History>> = _userHistories.asStateFlow()

    val recipients: StateFlow<List<User>> =
        userViewModel.users
            .map { list -> list.filter { it.role == UserRole.Recipient } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    private val _materialParticipationList = MutableStateFlow<List<MaterialParticipation>>(emptyList())
    val materialParticipationList: StateFlow<List<MaterialParticipation>> = _materialParticipationList

    internal val _clientToken = MutableStateFlow<String?>(null)
    val clientToken: StateFlow<String?> = _clientToken.asStateFlow()

    private val _donations = MutableStateFlow<List<Donation>>(emptyList())
    val donations: StateFlow<List<Donation>> = _donations

    init {
        viewModelScope.launch {
            clientTokenViewModel.clientToken.collect { token ->
                Log.d("PaymentDebug", "Client token updated: $token")
                _clientToken.value = token
                token?.let {
                    val orderId = clientTokenViewModel.orderId.value
                    Log.d("PaymentDebug", "OrderId after token: $orderId")
                    orderId?.let { id ->
                        fetchFondyPaymentStatus(id)
                    }
                }
            }
        }

        viewModelScope.launch {
            donationViewModel.donations.collect {
                _donations.value = it
            }
        }

        viewModelScope.launch {
            materialParticipationViewModel.materialParticipation.collect {
                _materialParticipationList.value = it
            }
        }
    }

    fun getCardByUserId(userId: Int): Card? {
        return cardViewModel.cards.value.find { it.idRecipient == userId }
    }

    fun toggleUserBlock(user: User) {
        val newState = !(user.isBlocked ?: false)

        userViewModel.updateUserBlock(user.id!!, newState) { success ->
            if (success) {
            }
        }

        binViewModel.create(
            Bin(
                idRecipient = user.id
            )
        ){}
    }

    fun updateMaterialStatus(helpId: Int, newStatus: MaterialParticipationStatus) {
        viewModelScope.launch {
            try {
                // Знаходження поточного participation
                val current = materialParticipationList.value
                    .find { it.idHelp == helpId }

                if (current == null) {
                    Log.e("MainViewModel", "MaterialParticipation not found for helpId=$helpId")
                    return@launch
                }

                // Створення оновленого об'єкту
                val updated = current.copy(status = newStatus)

                // update
                materialParticipationViewModel.update(updated) { success ->
                    if (success) {
                        Log.d("MainViewModel", "Status updated to $newStatus")

                        // 4. Оновлюємо локальний StateFlow (щоб UI відразу змінився)
                        _materialParticipationList.value =
                            _materialParticipationList.value.map {
                                if (it.id == updated.id) updated else it
                            }
                    } else {
                        Log.e("MainViewModel", "Failed to update status")
                    }
                }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error updating status: ${e.message}")
            }
        }
    }

    fun startDonation(amount: Double, helpId: Int?) {
        if (amount <= 0) return
        val volunteerId = currentUserId.value ?: return
        val donation = Donation(null, amount, volunteerId, helpId ?: return)
        val requestId = helpId ?: return

        val userToken = userViewModel.getToken()
        if (userToken.isNullOrBlank()) {
            Log.e("DonationDebug", "Auth token missing! Cannot create donation")
            return
        }

        donationViewModel.updateAuthHeader("$userToken")
        donationViewModel.create(donation) { createdDonation ->
            if (createdDonation != null) {
                Log.d("DonationDebug", "Donation created: $createdDonation")
                addHelpSum(requestId, amount)
                fetchClientToken(amount)
            } else {
                Log.e("DonationDebug", "Donation creation failed")
            }
        }
    }

    fun addHelpSum(requestId: Int, amount: Double) {
        helpViewModel.getById(requestId) { request ->
            when (request) {
                is Help.Financial -> {
                    val updated = request.copy(
                        collected = (request.collected ?: 0.0) + amount
                    )

                    helpViewModel.update(updated)
                }

                else -> {
                    Log.e("DonationDebug", "Help is not Financial type")
                }
            }
        }
    }

    fun fetchClientToken(amount: Double) {
        Log.d("PaymentDebug", "Calling fetchClientToken for amount: $amount")
        clientTokenViewModel.fetchClientToken(amount)
    }

    fun fetchFondyPaymentStatus(orderId: String) {
        Log.d("PaymentDebug", "Calling fetchFondyPaymentStatus for orderId: $orderId")
        clientTokenViewModel.fetchFondyPaymentStatus(orderId)
    }

    fun createMaterialParticipation(
        materialParticipation: MaterialParticipation
    ){
        materialParticipationViewModel.create(materialParticipation){}
    }

    fun getAllDonation() {
        val token = userViewModel.getToken()
        token?.let {
            donationViewModel.updateAuthHeader(it)
        }
        donationViewModel.getAll()
    }

    fun clearClientToken() {
        _clientToken.value = null
    }

    fun loadCards(){
        cardViewModel.getAll()
    }

    fun ensureHistoryForHelp(help: Help) {
        val helpId = help.id ?: return

        viewModelScope.launch {
            try {
                // Перевірка
                if (_userHistories.value.any { it.idRequest == helpId }) {
                    Log.d("History", "Already exists locally for requestId=$helpId")
                    return@launch
                }

                // Отримання актуальних історій
                historyViewModel.getAll()

                val historiesFromServer = historyViewModel.history.first()

                // Перевірка на сервері
                val existingHistory = historiesFromServer.find { it.idRequest == helpId }

                if (existingHistory != null) {
                    Log.d("History", "Already exists on server for requestId=$helpId")

                    _userHistories.value = _userHistories.value
                        .filterNot { it.idRequest == helpId } + existingHistory

                    return@launch
                }

                // Захист від дублювання
                if (_userHistories.value.any { it.idRequest == helpId }) {
                    return@launch
                }

                Log.d("History", "Creating in DB for requestId=$helpId")

                historyViewModel.create(
                    History(
                        status = HistoryStatus.Pending,
                        idAdmin = null,
                        idRequest = helpId
                    )
                ) { newHistory ->
                    newHistory?.let {
                        // Додається, якщо ще не існує
                        if (_userHistories.value.none { h -> h.idRequest == helpId }) {
                            _userHistories.value = _userHistories.value + it
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("History", "Failed to ensure history: ${e.message}")
            }
        }
    }

    fun getHelpByIdFlow(helpId: Int): StateFlow<Help?> {
        return _helpsList.map { list -> list.find { it.id == helpId } }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun getDonationByIdFlow(donationId: Int): StateFlow<Donation?> {
        return _donations.map { list -> list.find { it.id == donationId } }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun getAllMaterialParticipation(){
        materialParticipationViewModel.getAll()
    }

    fun getMaterialParticipationByHelpId(helpId: Int): StateFlow<MaterialParticipation?> {
        return materialParticipationList
            .map { list -> list.find { it.idHelp == helpId } }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                initialValue = null
            )
    }

    fun loadHelpsForUser(userId: Int) {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Loading helps for user $userId...")

                // потрібно спершу завантажити дані з API
                helpViewModel.getAll()
                delay(500)

                val helps = helpViewModel.helps.value.filter { it.idRecipient == userId }
                _helpsList.value = helps

                Log.d("MainViewModel", "Loaded helps: ${helps.size} items")
                helps.forEach { Log.d("MainViewModel", "Help: $it") }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load helps: ${e.message}")
                _helpsList.value = emptyList()
            }
        }
    }

    fun loadMaterialParticipationsForRecipient(userId: Int) {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Loading material participations for user $userId...")

                // Всі матеріальні допомоги користувача
                val materialHelps = helpViewModel.helps
                    .filter { list -> list.isNotEmpty() } // затримка для завантаження допомог
                    .first() // отримання першого непорожнього списку
                    .filter { it.idRecipient == userId && it is Help.Material }

                Log.d("MainViewModel", "Material helps for user: ${materialHelps.size} items")

                if (materialHelps.isEmpty()) {
                    _materialParticipationList.value = emptyList()
                    Log.d("MainViewModel", "No material helps, skipping participations load")
                    return@launch
                }

                // id допомог
                val materialHelpIds = materialHelps.map { it.id }

                // Завантаження participations через callback
                val userMps = mutableListOf<MaterialParticipation>()
                val deferredList = materialHelpIds.map { helpId ->
                    async {
                        suspendCancellableCoroutine<MaterialParticipation?> { cont ->
                            materialParticipationViewModel.getById(helpId) { mp ->
                                cont.resume(mp, onCancellation = null)
                            }
                        }
                    }
                }

                // Очікування всіх результатів
                userMps.addAll(deferredList.awaitAll().filterNotNull())

                _materialParticipationList.value = userMps
                Log.d("MainViewModel", "Loaded material participations: ${userMps.size} items")
                userMps.forEach { Log.d("MainViewModel", "MaterialParticipation: $it") }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load material participations: ${e.message}")
                _materialParticipationList.value = emptyList()
            }
        }
    }

    fun getAllHelps() {
        viewModelScope.launch {
            _isHelpsLoading.value = true
            helpViewModel.getAll()
            helpViewModel.helps.collect { helps ->
                _helpsList.value = helps
                _isHelpsLoading.value = false
            }
        }
    }

    fun getAllHistories() {
        viewModelScope.launch {
            historyViewModel.getAll()
            historyViewModel.history.collect { history ->
                _userHistories.value = history
            }
        }
    }

    fun updateCurrentUserId(id: Int?) {
        _currentUserId.value = id
    }

    fun deleteHelp(id: Int, onResult: ((Boolean) -> Unit)? = null) {
        helpViewModel.delete(id, onResult)
    }

    fun createHistory(history: History, onResult: (History?) -> Unit){
        historyViewModel.create(history, onResult)
    }

    fun updateHelp(help: Help, onResult: ((Boolean) -> Unit)? = null) {
        helpViewModel.update(help, onResult)
    }

    fun createUser(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        telegram: String?,
        password: String,
        hasCard: Boolean,
        onUserCreated: (Int?) -> Unit
    ) {
        userViewModel.create(firstName, lastName, email, phone, telegram, password, hasCard, onUserCreated)
    }

    fun authenticateUser(
        email: String,
        password: String,
        onResult: (success: Boolean, role: UserRole?) -> Unit
    ) {
        userViewModel.authenticate(email, password) { success, authRole ->
            if (success) {
                userViewModel.getToken()?.let { token ->
                    cardViewModel.updateAuthHeader(token)
                    Log.d("MainViewModel", "CardAPI authHeader updated: $token")
                    helpViewModel.updateAuthHeader(token)
                    Log.d("MainViewModel", "HelpAPI authHeader updated: $token")
                    materialParticipationViewModel.updateAuthHeader(token)
                    Log.d("MainViewModel", "MaterialParticipationAPI authHeader updated: $token")
                    historyViewModel.updateAuthHeader(token)
                    Log.d("MainViewModel", "HistoryAPI authHeader updated: $token")
                }

                onResult(true, authRole)
            } else {
                onResult(false, null)
            }
        }
    }

    fun createCard(
        maskedNumber: String,
        validityPeriod: YearMonth,
        cvv2: Int,
        idRecipient: Int?
    ){
        cardViewModel.create(maskedNumber, validityPeriod, cvv2, idRecipient!!)
    }

    fun createHelp(help: Help, onResult: (Help?) -> Unit) {
        helpViewModel.create(help, onResult)
    }

    fun getUserById(id: Int, onResult: (User?) -> Unit) {
        userViewModel.getById(id, onResult)
    }

    fun loadUsers() {
        userViewModel.getAll()
    }

    fun updateUser(
        user: User,
        onResult: ((Boolean) -> Unit)? = null
    ) {
        userViewModel.updateUser(
            user = user,
            onResult = onResult
        )
    }

    fun updateCard(
        card: Card,
        onResult: ((Boolean) -> Unit)? = null
    ) {
        cardViewModel.updateCard(
            card = card,
            onResult = onResult
        )
    }

    fun loadCvv2(context: Context, userId: Int, callback: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = getCvv2File(context, userId)
                val cvv = if (file.exists()) file.readText() else null

                withContext(Dispatchers.Main) {
                    callback(cvv)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }
}