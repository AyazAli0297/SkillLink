package com.example.skilllink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilllink.data.models.*
import com.example.skilllink.ui.theme.*
import com.example.skilllink.viewmodel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    amount: Double,
    service: String,
    providerName: String,
    bookingData: Map<String, Any>,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: (String) -> Unit
) {
    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentMethods by paymentViewModel.paymentMethods.collectAsState()
    val loading by paymentViewModel.loading.collectAsState()
    val paymentStatus by paymentViewModel.paymentStatus.collectAsState()
    val errorMessage by paymentViewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }

    // Handle payment status changes
    LaunchedEffect(paymentStatus) {
        when (paymentStatus) {
            PaymentStatus.COMPLETED -> {
                paymentViewModel.clearPaymentStatus()
                onPaymentSuccess()
            }
            PaymentStatus.FAILED -> {
                paymentViewModel.clearPaymentStatus()
                errorMessage?.let { onPaymentFailed(it) }
            }
            else -> {}
        }
    }

    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
            paymentViewModel.clearErrorMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryRed, PrimaryPurple)
                )
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                }
                Text(
                    "Payment",
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Main content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Payment Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGrayBackground)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Payment Summary",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Service:", color = Gray)
                                Text(service, color = DarkGray, fontWeight = FontWeight.Medium)
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Provider:", color = Gray)
                                Text(providerName, color = DarkGray, fontWeight = FontWeight.Medium)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                Modifier,
                                DividerDefaults.Thickness,
                                color = Gray.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Amount:",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGray
                                )
                                Text(
                                    "Rs. ${amount.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryRed
                                )
                            }
                        }
                    }
                }

                // Payment Methods Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Payment Methods",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray
                        )
                        TextButton(onClick = { showAddPaymentDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = PrimaryRed)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New", color = PrimaryRed)
                        }
                    }
                }

                // Payment Methods List
                if (paymentMethods.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = LightGrayBackground)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.CreditCard,
                                    contentDescription = "No Payment Methods",
                                    tint = Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No payment methods added",
                                    color = Gray,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Add a payment method to continue",
                                    color = Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(paymentMethods) { method ->
                        PaymentMethodCard(
                            paymentMethod = method,
                            isSelected = selectedPaymentMethod?.id == method.id,
                            onSelect = { selectedPaymentMethod = method },
                            onDelete = { paymentViewModel.removePaymentMethod(method.id) }
                        )
                    }
                }

                // Pay Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            selectedPaymentMethod?.let { method ->
                                paymentViewModel.processPayment(amount, method, bookingData)
                            }
                        },
                        enabled = selectedPaymentMethod != null && !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Pay Rs. ${amount.toInt()}",
                                color = White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Add Payment Method Dialog
        if (showAddPaymentDialog) {
            AddPaymentMethodDialog(
                onDismiss = { showAddPaymentDialog = false },
                onSave = { method ->
                    paymentViewModel.addPaymentMethod(method)
                    showAddPaymentDialog = false
                }
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryRed.copy(alpha = 0.1f) else White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, PrimaryRed)
        } else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect,
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = when (paymentMethod.type) {
                            PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> Icons.Filled.CreditCard
                            PaymentType.JAZZ_CASH -> Icons.Filled.Phone
                            PaymentType.EASY_PAISA -> Icons.Filled.AccountBalance
                        },
                        contentDescription = paymentMethod.type.name,
                        tint = if (isSelected) PrimaryRed else Gray
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = when (paymentMethod.type) {
                                PaymentType.CREDIT_CARD -> "Credit Card"
                                PaymentType.DEBIT_CARD -> "Debit Card"
                                PaymentType.JAZZ_CASH -> "JazzCash"
                                PaymentType.EASY_PAISA -> "EasyPaisa"
                            },
                            fontWeight = FontWeight.Medium,
                            color = DarkGray
                        )
                        Text(
                            text = when (paymentMethod.type) {
                                PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> 
                                    "**** **** **** ${paymentMethod.cardNumber.takeLast(4)}"
                                PaymentType.JAZZ_CASH -> paymentMethod.jazzCashNumber
                                PaymentType.EASY_PAISA -> paymentMethod.easyPaisaNumber
                            },
                            color = Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Gray)
                }
            }
            
            if (paymentMethod.isDefault) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Default",
                    color = PrimaryRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }
        }
    }
}
