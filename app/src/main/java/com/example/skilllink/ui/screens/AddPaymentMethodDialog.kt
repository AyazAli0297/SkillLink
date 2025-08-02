package com.example.skilllink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.skilllink.data.models.PaymentMethod
import com.example.skilllink.data.models.PaymentType
import com.example.skilllink.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentMethodDialog(
    onDismiss: () -> Unit,
    onSave: (PaymentMethod) -> Unit
) {
    var selectedType by remember { mutableStateOf(PaymentType.CREDIT_CARD) }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var jazzCashNumber by remember { mutableStateOf("") }
    var easyPaisaNumber by remember { mutableStateOf("") }
    var showCvv by remember { mutableStateOf(false) }

    fun isFormValid(): Boolean {
        return when (selectedType) {
            PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> {
                cardNumber.length >= 16 && 
                expiryDate.length >= 5 && 
                cvv.length >= 3 && 
                cardHolderName.isNotBlank()
            }
            PaymentType.JAZZ_CASH -> jazzCashNumber.length >= 11
            PaymentType.EASY_PAISA -> easyPaisaNumber.length >= 11
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Add Payment Method",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                // Payment Type Selector
                Text(
                    "Payment Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                // First Row: Credit and Debit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == PaymentType.CREDIT_CARD,
                        onClick = { selectedType = PaymentType.CREDIT_CARD },
                        label = {
                            Text(
                                "Credit",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRed,
                            selectedLabelColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilterChip(
                        selected = selectedType == PaymentType.DEBIT_CARD,
                        onClick = { selectedType = PaymentType.DEBIT_CARD },
                        label = {
                            Text(
                                "Debit",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRed,
                            selectedLabelColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Second Row: Jazz and Easypaisa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == PaymentType.JAZZ_CASH,
                        onClick = { selectedType = PaymentType.JAZZ_CASH },
                        label = {
                            Text(
                                "Jazz",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRed,
                            selectedLabelColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilterChip(
                        selected = selectedType == PaymentType.EASY_PAISA,
                        onClick = { selectedType = PaymentType.EASY_PAISA },
                        label = {
                            Text(
                                "Easypaisa",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryRed,
                            selectedLabelColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dynamic form based on payment type
                when (selectedType) {
                    PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> {
                        // Card Holder Name
                        OutlinedTextField(
                            value = cardHolderName,
                            onValueChange = { cardHolderName = it },
                            label = { Text("Card Holder Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryRed,
                                cursorColor = PrimaryRed
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Card Number
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { 
                                if (it.length <= 19) { // Max length with spaces
                                    cardNumber = formatCardNumber(it)
                                }
                            },
                            label = { Text("Card Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("1234 5678 9012 3456") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryRed,
                                cursorColor = PrimaryRed
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Expiry Date
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { 
                                    if (it.length <= 5) {
                                        expiryDate = formatExpiryDate(it)
                                    }
                                },
                                label = { Text("MM/YY") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("12/25") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    cursorColor = PrimaryRed
                                )
                            )

                            // CVV
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { 
                                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                        cvv = it
                                    }
                                },
                                label = { Text("CVV") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = if (showCvv) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { showCvv = !showCvv }) {
                                        Icon(
                                            if (showCvv) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = if (showCvv) "Hide CVV" else "Show CVV"
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    cursorColor = PrimaryRed
                                )
                            )
                        }
                    }

                    PaymentType.JAZZ_CASH -> {
                        OutlinedTextField(
                            value = jazzCashNumber,
                            onValueChange = { 
                                if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                                    jazzCashNumber = it
                                }
                            },
                            label = { Text("JazzCash Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            placeholder = { Text("03001234567") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryRed,
                                cursorColor = PrimaryRed
                            )
                        )
                    }

                    PaymentType.EASY_PAISA -> {
                        OutlinedTextField(
                            value = easyPaisaNumber,
                            onValueChange = { 
                                if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                                    easyPaisaNumber = it
                                }
                            },
                            label = { Text("EasyPaisa Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            placeholder = { Text("03001234567") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryRed,
                                cursorColor = PrimaryRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryRed
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            if (isFormValid()) {
                                val paymentMethod = PaymentMethod(
                                    type = selectedType,
                                    cardNumber = if (selectedType in listOf(PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD)) 
                                        cardNumber.replace(" ", "") else "",
                                    expiryDate = expiryDate,
                                    cvv = cvv,
                                    cardHolderName = cardHolderName,
                                    jazzCashNumber = jazzCashNumber,
                                    easyPaisaNumber = easyPaisaNumber
                                )
                                onSave(paymentMethod)
                            }
                        },
                        enabled = isFormValid(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                    ) {
                        Text("Save", color = White)
                    }
                }
            }
        }
    }
}

private fun formatCardNumber(input: String): String {
    val digitsOnly = input.replace(" ", "")
    return digitsOnly.chunked(4).joinToString(" ")
}

private fun formatExpiryDate(input: String): String {
    val digitsOnly = input.replace("/", "")
    return if (digitsOnly.length >= 2) {
        "${digitsOnly.take(2)}/${digitsOnly.drop(2).take(2)}"
    } else {
        digitsOnly
    }
}
