package com.example.skilllink.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.skilllink.ui.screens.PaymentScreen

object PaymentScreenLauncher {
    @Composable
    fun LaunchPaymentScreen(
        amount: Double,
        service: String,
        providerName: String,
        bookingData: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        onBack: () -> Unit
    ) {
        PaymentScreen(
            amount = amount,
            service = service,
            providerName = providerName,
            bookingData = bookingData,
            onBack = onBack,
            onPaymentSuccess = onSuccess,
            onPaymentFailed = onFailure
        )
    }
}

@Composable
fun rememberPaymentLauncher(
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
): PaymentLauncher {
    return remember {
        PaymentLauncher(onSuccess, onFailure)
    }
}

class PaymentLauncher(
    private val onSuccess: () -> Unit,
    private val onFailure: (String) -> Unit
) {
    @Composable
    fun LaunchPayment(
        amount: Double,
        service: String,
        providerName: String,
        bookingData: Map<String, Any>,
        onBack: () -> Unit
    ) {
        PaymentScreen(
            amount = amount,
            service = service,
            providerName = providerName,
            bookingData = bookingData,
            onBack = onBack,
            onPaymentSuccess = onSuccess,
            onPaymentFailed = onFailure
        )
    }
}
