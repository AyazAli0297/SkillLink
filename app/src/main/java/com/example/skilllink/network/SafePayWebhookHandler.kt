package com.example.skilllink.network

import com.example.skilllink.data.models.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SafePayWebhookHandler {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    fun handleWebhook(webhookData: SafePayWebhookData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (webhookData.event) {
                    "checkout.paid" -> handlePaymentSuccess(webhookData)
                    "checkout.failed" -> handlePaymentFailure(webhookData)
                    "checkout.cancelled" -> handlePaymentCancellation(webhookData)
                    else -> {
                        // Log unknown event
                        println("Unknown SafePay webhook event: ${webhookData.event}")
                    }
                }
            } catch (e: Exception) {
                println("Error handling SafePay webhook: ${e.message}")
            }
        }
    }
    
    private suspend fun handlePaymentSuccess(webhookData: SafePayWebhookData) {
        val orderId = webhookData.order_id
        val tracker = webhookData.tracker
        
        // Update transaction status
        updateTransactionStatus(orderId, PaymentStatus.COMPLETED, tracker.token)
        
        // Update booking status
        updateBookingPaymentStatus(orderId, "Paid", tracker.token)
        
        // You can add additional logic here like sending confirmation emails, notifications, etc.
    }
    
    private suspend fun handlePaymentFailure(webhookData: SafePayWebhookData) {
        val orderId = webhookData.order_id
        val tracker = webhookData.tracker
        
        // Update transaction status
        updateTransactionStatus(orderId, PaymentStatus.FAILED, tracker.token)
        
        // Update booking status
        updateBookingPaymentStatus(orderId, "Failed", tracker.token)
    }
    
    private suspend fun handlePaymentCancellation(webhookData: SafePayWebhookData) {
        val orderId = webhookData.order_id
        val tracker = webhookData.tracker
        
        // Update transaction status
        updateTransactionStatus(orderId, PaymentStatus.CANCELLED, tracker.token)
        
        // Update booking status
        updateBookingPaymentStatus(orderId, "Cancelled", tracker.token)
    }
    
    private suspend fun updateTransactionStatus(orderId: String, status: PaymentStatus, token: String) {
        try {
            val querySnapshot = firestore.collection("transactions")
                .whereEqualTo("transactionId", orderId)
                .get()
                .await()
            
            for (document in querySnapshot.documents) {
                document.reference.update(
                    mapOf(
                        "status" to status.name,
                        "transactionId" to token
                    )
                ).await()
            }
        } catch (e: Exception) {
            println("Error updating transaction status: ${e.message}")
        }
    }
    
    private suspend fun updateBookingPaymentStatus(orderId: String, paymentStatus: String, token: String) {
        try {
            val querySnapshot = firestore.collection("bookings")
                .whereEqualTo("transactionId", orderId)
                .get()
                .await()
            
            for (document in querySnapshot.documents) {
                document.reference.update(
                    mapOf(
                        "paymentStatus" to paymentStatus,
                        "transactionId" to token
                    )
                ).await()
            }
        } catch (e: Exception) {
            println("Error updating booking payment status: ${e.message}")
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SafePayWebhookHandler? = null
        
        fun getInstance(): SafePayWebhookHandler {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SafePayWebhookHandler().also { INSTANCE = it }
            }
        }
    }
}
