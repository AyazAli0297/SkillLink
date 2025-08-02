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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilllink.data.models.*
import com.example.skilllink.ui.theme.*
import com.example.skilllink.viewmodel.PaymentViewModel

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    onTransactionHistory: () -> Unit = {}
) {
    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentMethods by paymentViewModel.paymentMethods.collectAsState()
    val transactions by paymentViewModel.transactions.collectAsState()
    val loading by paymentViewModel.loading.collectAsState()
    val errorMessage by paymentViewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    var showAddPaymentDialog by remember { mutableStateOf(false) }

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
                    colors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "Wallet",
                    color = Color.White,
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
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Wallet Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFB31217).copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = Color(0xFFB31217),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Total Transactions",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                "${transactions.size}",
                                color = Color(0xFF2A0845),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = onTransactionHistory,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB31217))
                            ) {
                                Icon(Icons.Filled.History, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("View Transaction History", color = Color.White)
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A0845)
                        )
                        TextButton(onClick = { showAddPaymentDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color(0xFFB31217))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New", color = Color(0xFFB31217))
                        }
                    }
                }

                // Payment Methods List
                if (loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFB31217))
                        }
                    }
                } else if (paymentMethods.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.CreditCard,
                                    contentDescription = "No Payment Methods",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No payment methods",
                                    color = Color(0xFF2A0845),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Add a payment method to make quick payments",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { showAddPaymentDialog = true },
                                    shape = RoundedCornerShape(25.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB31217))
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Payment Method", color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    items(paymentMethods) { method ->
                        WalletPaymentMethodCard(
                            paymentMethod = method,
                            onDelete = { paymentViewModel.removePaymentMethod(method.id) },
                            onSetDefault = { paymentViewModel.setDefaultPaymentMethod(method.id) }
                        )
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
fun WalletPaymentMethodCard(
    paymentMethod: PaymentMethod,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (paymentMethod.isDefault) 
                Color(0xFFB31217).copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (paymentMethod.type) {
                            PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> Icons.Filled.CreditCard
                            PaymentType.JAZZ_CASH -> Icons.Filled.Phone
                            PaymentType.EASY_PAISA -> Icons.Filled.AccountBalance
                        },
                        contentDescription = paymentMethod.type.name,
                        tint = if (paymentMethod.isDefault) Color(0xFFB31217) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = when (paymentMethod.type) {
                                PaymentType.CREDIT_CARD -> "Credit Card"
                                PaymentType.DEBIT_CARD -> "Debit Card"
                                PaymentType.JAZZ_CASH -> "JazzCash"
                                PaymentType.EASY_PAISA -> "EasyPaisa"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2A0845)
                        )
                        Text(
                            text = when (paymentMethod.type) {
                                PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> 
                                    "**** **** **** ${paymentMethod.cardNumber.takeLast(4)}"
                                PaymentType.JAZZ_CASH -> paymentMethod.jazzCashNumber
                                PaymentType.EASY_PAISA -> paymentMethod.easyPaisaNumber
                            },
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        if (paymentMethod.type in listOf(PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD)) {
                            Text(
                                text = "Expires ${paymentMethod.expiryDate}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Row {
                    if (!paymentMethod.isDefault) {
                        TextButton(onClick = onSetDefault) {
                            Text("Set Default", color = Color(0xFFB31217), fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
            }
            
            if (paymentMethod.isDefault) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFB31217).copy(alpha = 0.1f)
                ) {
                    Text(
                        "Default Payment Method",
                        color = Color(0xFFB31217),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
} 