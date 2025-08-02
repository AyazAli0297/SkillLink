package com.example.skilllink.config

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object PaymentConfig {
    
    // SafePay configuration - Replace with your actual SafePay credentials
    const val SAFEPAY_BASE_URL = "https://sandbox.api.getsafepay.com/"
    const val SAFEPAY_API_KEY = "sec_0a48c02b-63a9-46e2-863c-bfebbf5a8a40" // Get from SafePay dashboard
    const val SAFEPAY_SECRET_KEY = "885abf64128726b1889f89ced659c1f9231a974f387c565ac14dec2acb715151" // Get from SafePay dashboard
    const val SAFEPAY_WEBHOOK_URL = "https://your-domain.com/webhook/safepay" // Your webhook URL
    const val SAFEPAY_REDIRECT_URL = "skilllink://payment/success" // Deep link for app
    
    // Supported payment methods in SafePay
    val SUPPORTED_PAYMENT_METHODS = listOf(
        "VISA",
        "MASTERCARD", 
        "JAZZ_CASH",
        "EASY_PAISA",
        "HBL_CONNECT",
        "BANK_ALFALAH",
        "UBL_OMNI"
    )
    
    // Currency
    const val DEFAULT_CURRENCY = "PKR"
    
    // Minimum and maximum transaction amounts
    const val MIN_TRANSACTION_AMOUNT = 10.0
    const val MAX_TRANSACTION_AMOUNT = 500000.0
    
    // Session timeout in milliseconds
    const val PAYMENT_SESSION_TIMEOUT = 300000L // 5 minutes
    
    fun getEncryptedPreferences(context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        return EncryptedSharedPreferences.create(
            context,
            "payment_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }
    
    fun generateAuthToken(): String {
        // Generate authentication token for SafePay API
        // SafePay uses Basic Auth with API key
        val credentials = "$SAFEPAY_API_KEY:"
        val encodedCredentials = android.util.Base64.encodeToString(
            credentials.toByteArray(), 
            android.util.Base64.NO_WRAP
        )
        return "Basic $encodedCredentials"
    }
    
    fun generateOrderId(): String {
        // Generate unique order ID
        return "SKILL_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    fun validateCardNumber(cardNumber: String): Boolean {
        // Luhn algorithm for card validation
        val cleanNumber = cardNumber.replace(" ", "").replace("-", "")
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false
        
        var sum = 0
        var alternate = false
        
        for (i in cleanNumber.length - 1 downTo 0) {
            var digit = cleanNumber[i].toString().toIntOrNull() ?: return false
            
            if (alternate) {
                digit *= 2
                if (digit > 9) digit = (digit / 10) + (digit % 10)
            }
            
            sum += digit
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }
    
    fun validateCVV(cvv: String, cardType: String): Boolean {
        return when (cardType.uppercase()) {
            "AMEX" -> cvv.length == 4 && cvv.all { it.isDigit() }
            else -> cvv.length == 3 && cvv.all { it.isDigit() }
        }
    }
    
    fun validateExpiryDate(expiryDate: String): Boolean {
        val regex = Regex("^(0[1-9]|1[0-2])/([0-9]{2})$")
        if (!regex.matches(expiryDate)) return false
        
        val parts = expiryDate.split("/")
        val month = parts[0].toInt()
        val year = parts[1].toInt()
        
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        
        return if (year > currentYear) {
            true
        } else if (year == currentYear) {
            month >= currentMonth
        } else {
            false
        }
    }
    
    fun maskCardNumber(cardNumber: String): String {
        if (cardNumber.length < 8) return cardNumber
        val clean = cardNumber.replace(" ", "")
        return "**** **** **** ${clean.takeLast(4)}"
    }
    
    fun getCardType(cardNumber: String): String {
        val clean = cardNumber.replace(" ", "").replace("-", "")
        return when {
            clean.startsWith("4") -> "VISA"
            clean.startsWith("5") || clean.startsWith("2") -> "MASTERCARD"
            clean.startsWith("3") -> "AMEX"
            else -> "UNKNOWN"
        }
    }
}
