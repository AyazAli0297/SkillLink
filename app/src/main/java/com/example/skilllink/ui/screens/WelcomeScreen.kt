package com.example.skilllink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skilllink.R
import androidx.compose.ui.draw.alpha

@Composable
fun WelcomeScreen(onSignIn: () -> Unit, onSignUp: () -> Unit) {
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
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Name
            Text(
                text = "SkillLink",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Tagline (centered)
            Text(
                text = "Connecting Local Skills With Real Needs",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("SIGN IN", color = Color.White, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSignUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("SIGN UP", color = Color(0xFF2A0845), fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Login with Social Media",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.alpha(0f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().alpha(0f)
            ) {
                // Facebook icon
                Image(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(48.dp).padding(horizontal = 12.dp)
                )
                // Google icon
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(48.dp).padding(horizontal = 12.dp)
                )
            }
        }
    }
} 