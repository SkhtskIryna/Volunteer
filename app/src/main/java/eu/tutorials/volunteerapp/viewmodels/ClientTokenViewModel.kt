package eu.tutorials.volunteerapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.volunteerapp.api.ClientTokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientTokenViewModel : ViewModel() {

    private val apiClientToken = ClientTokenProvider()

    private val _clientTokenPayment = MutableStateFlow<String?>(null)
    val clientToken: StateFlow<String?> = _clientTokenPayment.asStateFlow()

    private val _orderId = MutableStateFlow<String?>(null)
    val orderId: StateFlow<String?> = _orderId.asStateFlow()

    private val _paymentStatus = MutableStateFlow<String?>(null)

    fun fetchClientToken(amount: Double) {
        viewModelScope.launch {
            Log.d("PaymentDebug", "Fetching token for amount: $amount")
            val response = apiClientToken.fetchFondyCheckoutUrl(amount)
            Log.d("PaymentDebug", "Token received: $response")
            _clientTokenPayment.value = response?.checkout_url
            _orderId.value = response?.order_id
        }
    }

    fun fetchFondyPaymentStatus(orderId: String) {
        viewModelScope.launch {
            Log.d("PaymentDebug", "Fetching payment status for orderId: $orderId")
            val status = apiClientToken.fetchFondyPaymentStatus(orderId)
            Log.d("PaymentDebug", "Payment status received: $status")
            _paymentStatus.value = status
        }
    }
}