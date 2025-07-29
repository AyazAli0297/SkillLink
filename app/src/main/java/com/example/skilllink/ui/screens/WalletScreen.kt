package com.example.skilllink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun WalletScreen(onBack: () -> Unit) {
    val points = 120
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
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .background(Color.White, RoundedCornerShape(40.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Wallet", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF2A0845))
            Spacer(modifier = Modifier.height(16.dp))
            Text("You have $points points for completed orders.", color = Color(0xFF2A0845), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB31217))) {
                Text("Back", color = Color.White)
            }
        }
    }
} 