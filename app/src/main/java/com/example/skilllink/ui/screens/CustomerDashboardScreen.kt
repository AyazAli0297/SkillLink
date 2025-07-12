package com.example.skilllink.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman

@Composable
fun CustomerDashboardScreen() {
    val selectedTab = remember { mutableStateOf(0) }
    val blue = Color(0xFF1877F3)
    val lightGray = Color(0xFFF5F6FA)
    val gradientColors = listOf(Color(0xFFB31217), Color(0xFF2A0845))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = 400f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dashboard",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { /* TODO: Settings */ }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
            // Card Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Quick Actions",
                        color = Color(0xFF2A0845),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { /* TODO: Book a Service */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = blue)
                        ) {
                            Text("Book a Service", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { /* TODO: View Bookings */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                        ) {
                            Text("View Bookings", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { /* TODO: Browse Pros */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                        ) {
                            Text("Browse Pros", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { /* TODO: Wallet */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                        ) {
                            Text("Wallet", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO: Contact Support */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                    ) {
                        Text("Contact Support", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                    }
                    // Our Offerings Section
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Our Offerings",
                        color = Color(0xFF2A0845),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Plumber
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFB31217)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Build,
                                    contentDescription = "Plumber",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Plumber", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                        // Electrician
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2A0845)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ElectricalServices,
                                    contentDescription = "Electrician",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Electrician", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                        // AC Guy
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1877F3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AcUnit,
                                    contentDescription = "AC Guy",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("AC Guy", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                        // Painter
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF5F6FA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FormatPaint,
                                    contentDescription = "Painter",
                                    tint = Color(0xFF2A0845),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Painter", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                        // Carpenter
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2A0845)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Handyman,
                                    contentDescription = "Carpenter",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Carpenter", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            // Bottom Navigation
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab.value == 0,
                    onClick = { selectedTab.value = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    selected = selectedTab.value == 1,
                    onClick = { selectedTab.value = 1 },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    selected = selectedTab.value == 2,
                    onClick = { selectedTab.value = 2 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    alwaysShowLabel = true
                )
            }
        }
    }
} 