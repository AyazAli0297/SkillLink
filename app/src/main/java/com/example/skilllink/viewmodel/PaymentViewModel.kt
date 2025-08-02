package com.example.skilllink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilllink.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class PaymentViewModel : ViewModel() {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val safePayGateway = com.example.skilllink.network.SafePayGateway.getInstance()
    
    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    
    private val _paymentStatus = MutableStateFlow<PaymentStatus?>(null)
    val paymentStatus: StateFlow<PaymentStatus?> = _paymentStatus
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(emptyList())
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions

    init {
        loadPaymentMethods()
        loadTransactions()
    }

    fun loadPaymentMethods() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val snapshot = firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("paymentMethods")
                        .get()
                        .await()
                    
                    val methods = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PaymentMethod::class.java)?.copy(id = doc.id)
                    }
                    _paymentMethods.value = methods
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load payment methods: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addPaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // If this is the first payment method, make it default
                    val isFirstMethod = _paymentMethods.value.isEmpty()
                    val methodToAdd = paymentMethod.copy(isDefault = isFirstMethod || paymentMethod.isDefault)
                    
                    // If setting as default, remove default from other methods
                    if (methodToAdd.isDefault) {
                        setAllMethodsNonDefault(currentUser.uid)
                    }
                    
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("paymentMethods")
                        .add(methodToAdd)
                        .await()
                    
                    loadPaymentMethods()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add payment method: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun removePaymentMethod(methodId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("paymentMethods")
                        .document(methodId)
                        .delete()
                        .await()
                    
                    loadPaymentMethods()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove payment method: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun setDefaultPaymentMethod(methodId: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Remove default from all methods
                    setAllMethodsNonDefault(currentUser.uid)
                    
                    // Set the selected method as default
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("paymentMethods")
                        .document(methodId)
                        .update("isDefault", true)
                        .await()
                    
                    loadPaymentMethods()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to set default payment method: ${e.message}"
            }
        }
    }

    private suspend fun setAllMethodsNonDefault(userId: String) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("paymentMethods")
                .whereEqualTo("isDefault", true)
                .get()
                .await()
            
            snapshot.documents.forEach { doc ->
                doc.reference.update("isDefault", false).await()
            }
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }

    fun processPayment(
        amount: Double,
        paymentMethod: PaymentMethod,
        bookingData: Map<String, Any>
    ) {
        viewModelScope.launch {
            _loading.value = true
            _paymentStatus.value = PaymentStatus.PROCESSING
            
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Create transaction record
                    val orderId = com.example.skilllink.config.PaymentConfig.generateOrderId()
                    val transaction = PaymentTransaction(
                        bookingId = "", // Will be set after booking creation
                        customerId = currentUser.uid,
                        skilledId = bookingData["skilledId"] as String,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        status = PaymentStatus.PROCESSING,
                        transactionDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
                        transactionId = orderId,
                        description = "Payment for ${bookingData["service"]}"
                    )
                    
                    // Process SafePay payment
                    val paymentResult = processSafePayPayment(transaction, currentUser)
                    
                    if (paymentResult.success) {
                        // Create booking with payment info
                        val bookingWithPayment = bookingData.toMutableMap().apply {
                            put("paymentStatus", "Paid")
                            put("transactionId", paymentResult.token ?: orderId)
                            put("paymentMethod", getPaymentMethodDisplayName(paymentMethod))
                            put("amount", amount)
                        }
                        
                        // Save booking
                        val bookingRef = firestore.collection("bookings").add(bookingWithPayment).await()
                        
                        // Update transaction with booking ID
                        val finalTransaction = transaction.copy(
                            bookingId = bookingRef.id,
                            status = PaymentStatus.COMPLETED,
                            transactionId = paymentResult.token ?: orderId
                        )
                        
                        // Save transaction
                        firestore.collection("transactions").add(finalTransaction).await()
                        
                        _paymentStatus.value = PaymentStatus.COMPLETED
                        loadTransactions()
                    } else {
                        _paymentStatus.value = PaymentStatus.FAILED
                        _errorMessage.value = paymentResult.message ?: paymentResult.error ?: "Payment failed"
                    }
                }
            } catch (e: Exception) {
                _paymentStatus.value = PaymentStatus.FAILED
                _errorMessage.value = "Payment processing failed: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun processSafePayPayment(
        transaction: PaymentTransaction,
        currentUser: com.google.firebase.auth.FirebaseUser
    ): SafePayResponse {
        return try {
            // Create SafePay customer
            val customer = SafePayCustomer(
                name = currentUser.displayName ?: "Customer",
                email = currentUser.email ?: "customer@skilllink.com",
                phone = "+92300000000", // You might want to get this from user profile
                country = "PK"
            )
            
            // Call real SafePay API
            val result = safePayGateway.createCheckout(
                amount = transaction.amount,
                customer = customer,
                orderId = transaction.transactionId
            )
            
            // For demo purposes, we'll still simulate some responses
            // Remove this simulation when you have real SafePay credentials
            if (result.success) {
                result
            } else {
                // Fallback simulation for demo
                val isSuccess = (1..10).random() <= 9
                if (isSuccess) {
                    SafePayResponse(
                        success = true,
                        token = "sp_demo_${System.currentTimeMillis()}",
                        checkout_url = "https://sandbox.getsafepay.com/checkout/demo",
                        payment_url = "https://sandbox.getsafepay.com/pay/demo",
                        message = "Checkout created successfully (Demo Mode)"
                    )
                } else {
                    SafePayResponse(
                        success = false,
                        message = "Payment failed. Please try again.",
                        error = "insufficient_funds"
                    )
                }
            }
        } catch (e: Exception) {
            SafePayResponse(
                success = false,
                message = "Network error: ${e.message}",
                error = "network_error"
            )
        }
    }

    private fun generateTransactionId(): String {
        return "SKILL_${System.currentTimeMillis()}"
    }

    private fun getPaymentMethodDisplayName(paymentMethod: PaymentMethod): String {
        return when (paymentMethod.type) {
            PaymentType.CREDIT_CARD -> "Credit Card ending in ${paymentMethod.cardNumber.takeLast(4)}"
            PaymentType.DEBIT_CARD -> "Debit Card ending in ${paymentMethod.cardNumber.takeLast(4)}"
            PaymentType.JAZZ_CASH -> "JazzCash - ${paymentMethod.jazzCashNumber}"
            PaymentType.EASY_PAISA -> "EasyPaisa - ${paymentMethod.easyPaisaNumber}"
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val snapshot = firestore.collection("transactions")
                        .whereEqualTo("customerId", currentUser.uid)
                        .get()
                        .await()
                    
                    val transactionsList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PaymentTransaction::class.java)?.copy(id = doc.id)
                    }
                    _transactions.value = transactionsList
                }
            } catch (e: Exception) {
                // Handle error silently or log
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearPaymentStatus() {
        _paymentStatus.value = null
    }
}
