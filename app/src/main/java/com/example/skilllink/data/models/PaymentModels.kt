package com.example.skilllink.data.models

data class PaymentMethod(
    val id: String = "",
    val type: PaymentType = PaymentType.CREDIT_CARD,
    val cardNumber: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val cardHolderName: String = "",
    val jazzCashNumber: String = "",
    val easyPaisaNumber: String = "",
    val isDefault: Boolean = false
)

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    JAZZ_CASH,
    EASY_PAISA
}

data class PaymentTransaction(
    val id: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val skilledId: String = "",
    val amount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod(),
    val status: PaymentStatus = PaymentStatus.PENDING,
    val transactionDate: String = "",
    val transactionId: String = "",
    val currency: String = "PKR",
    val description: String = ""
)

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED
}

data class SafePayRequest(
    val amount: Double,
    val currency: String = "PKR",
    val customer: SafePayCustomer,
    val order_id: String,
    val webhook: String? = null,
    val redirect_url: String? = null,
    val source: String = "mobile_app"
)

data class SafePayCustomer(
    val name: String,
    val email: String,
    val phone: String,
    val country: String = "PK"
)

data class SafePayResponse(
    val success: Boolean,
    val token: String? = null,
    val checkout_url: String? = null,
    val payment_url: String? = null,
    val message: String? = null,
    val error: String? = null
)

data class SafePayWebhookData(
    val event: String,
    val order_id: String,
    val tracker: SafePayTracker
)

data class SafePayTracker(
    val token: String,
    val amount: Int,
    val currency: String,
    val order_id: String,
    val customer: SafePayCustomer,
    val created_at: String,
    val state: String, // "pending", "paid", "failed", "cancelled"
    val fees: Int? = null,
    val net_amount: Int? = null
)
