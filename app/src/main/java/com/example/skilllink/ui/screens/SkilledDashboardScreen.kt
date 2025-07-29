package com.example.skilllink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.skilllink.navigation.Screen

@Composable
fun SkilledDashboardScreen(navController: NavHostController) {
    val cardBg = Color(0xFF444444) // Dark card
    val cardText = Color.White
    val completedFlag = Color(0xFF4CAF50) // Green
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
                )
            )
            .padding(16.dp)
    ) {
        // Dashboard Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dashboard", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(onClick = { /* Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate(Screen.ViewBookings.route) },
                    modifier = Modifier.weight(1f).padding(end = 8.dp).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("View Bookings", color = Color(0xFF2A0845), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { navController.navigate(Screen.Wallet.route) },
                    modifier = Modifier.weight(1f).padding(start = 8.dp).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Wallet", color = Color(0xFF2A0845), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Screen.ContactSupport.route) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Contact Support", color = Color(0xFF2A0845), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Orders Section
        Text("Your Orders", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        val dummyOrders = listOf(
            Order("Ceiling Fan Installation", "Completed", "Rs:800", 4.5),
            Order("SMD Lights Installation", "Appointed", "Rs:500", 4.8),
            Order("32-42 Inch LED TV or LCD Mounting", "Completed", "Rs:1250", 4.5)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(dummyOrders) { order ->
                OrderCard(
                    order = order,
                    titleColor = cardText,
                    statusColor = cardText,
                    priceColor = cardText,
                    cardBg = cardBg,
                    completedFlag = completedFlag
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class Order(val name: String, val status: String, val price: String, val rating: Double)

@Composable
fun OrderCard(
    order: Order,
    titleColor: Color = Color.White,
    statusColor: Color = Color.White,
    priceColor: Color = Color.White,
    cardBg: Color = Color(0xFF444444),
    completedFlag: Color = Color(0xFF4CAF50)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.name, fontWeight = FontWeight.Bold, color = titleColor, fontSize = 18.sp)
                    Text(order.status, color = statusColor, fontSize = 14.sp)
                    Text(order.price, color = priceColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text("⭐ ${order.rating}", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            if (order.status.equals("Completed", ignoreCase = true)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(completedFlag, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Completed", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
} 