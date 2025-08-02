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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilllink.data.models.*
import com.example.skilllink.ui.theme.*
import com.example.skilllink.viewmodel.PaymentViewModel

@Composable
fun TransactionHistoryScreen(
    onBack: () -> Unit
) {
    val paymentViewModel: PaymentViewModel = viewModel()
    val transactions by paymentViewModel.transactions.collectAsState()
    val loading by paymentViewModel.loading.collectAsState()

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
                    "Transaction History",
                    color = White,
                    fontSize = 24.sp,
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
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryRed)
                }
            } else if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Receipt,
                        contentDescription = "No Transactions",
                        tint = Gray,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "No transactions yet",
                        color = DarkGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your payment history will appear here",
                        color = Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions.sortedByDescending { it.transactionDate }) { transaction ->
                        TransactionCard(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: PaymentTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID: ${transaction.transactionId}",
                        fontSize = 12.sp,
                        color = Gray
                    )
                }
                
                StatusChip(status = transaction.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount",
                        fontSize = 12.sp,
                        color = Gray
                    )
                    Text(
                        text = "${transaction.currency} ${transaction.amount.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = when (transaction.status) {
                            PaymentStatus.COMPLETED -> Color(0xFF4CAF50)
                            PaymentStatus.FAILED, PaymentStatus.CANCELLED -> Color(0xFFF44336)
                            else -> DarkGray
                        }
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Date",
                        fontSize = 12.sp,
                        color = Gray
                    )
                    Text(
                        text = transaction.transactionDate,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = DarkGray
                    )
                }
            }
            
            if (transaction.paymentMethod.type != PaymentType.CREDIT_CARD || 
                transaction.paymentMethod.cardNumber.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (transaction.paymentMethod.type) {
                            PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> Icons.Filled.CreditCard
                            PaymentType.JAZZ_CASH -> Icons.Filled.Phone
                            PaymentType.EASY_PAISA -> Icons.Filled.AccountBalance
                        },
                        contentDescription = null,
                        tint = Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (transaction.paymentMethod.type) {
                            PaymentType.CREDIT_CARD -> "Credit Card ending in ${transaction.paymentMethod.cardNumber.takeLast(4)}"
                            PaymentType.DEBIT_CARD -> "Debit Card ending in ${transaction.paymentMethod.cardNumber.takeLast(4)}"
                            PaymentType.JAZZ_CASH -> "JazzCash - ${transaction.paymentMethod.jazzCashNumber}"
                            PaymentType.EASY_PAISA -> "EasyPaisa - ${transaction.paymentMethod.easyPaisaNumber}"
                        },
                        fontSize = 12.sp,
                        color = Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: PaymentStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        PaymentStatus.COMPLETED -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.1f),
            Color(0xFF4CAF50),
            "Completed"
        )
        PaymentStatus.PENDING -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.1f),
            Color(0xFFFF9800),
            "Pending"
        )
        PaymentStatus.PROCESSING -> Triple(
            Color(0xFF2196F3).copy(alpha = 0.1f),
            Color(0xFF2196F3),
            "Processing"
        )
        PaymentStatus.FAILED -> Triple(
            Color(0xFFF44336).copy(alpha = 0.1f),
            Color(0xFFF44336),
            "Failed"
        )
        PaymentStatus.CANCELLED -> Triple(
            Color(0xFF9E9E9E).copy(alpha = 0.1f),
            Color(0xFF9E9E9E),
            "Cancelled"
        )
        PaymentStatus.REFUNDED -> Triple(
            Color(0xFF9C27B0).copy(alpha = 0.1f),
            Color(0xFF9C27B0),
            "Refunded"
        )
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
