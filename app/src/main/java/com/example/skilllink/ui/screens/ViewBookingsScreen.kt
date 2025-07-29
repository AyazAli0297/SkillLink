package com.example.skilllink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skilllink.ui.screens.Order
import com.example.skilllink.ui.screens.OrderCard

@Composable
fun ViewBookingsScreen(onBack: () -> Unit) {
    val bookings = listOf(
        Order("Ceiling Fan Installation", "Completed", "Rs:800", 4.5),
        Order("SMD Lights Installation", "Appointed", "Rs:500", 4.8),
        Order("32-42 Inch LED TV or LCD Mounting", "Completed", "Rs:1250", 4.5)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Your Bookings",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(bookings) { order ->
                    OrderCard(
                        order = order,
                        titleColor = Color.White,
                        statusColor = Color.White,
                        priceColor = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB31217))
            ) {
                Text("Back", color = Color.White)
            }
        }
    }
} 