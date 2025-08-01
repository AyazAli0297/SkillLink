package com.example.skilllink.ui.screens

import android.content.Intent
import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedTextFieldDefaults


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

import coil.compose.AsyncImage

import com.example.skilllink.ui.theme.*
import com.example.skilllink.viewmodel.ProviderViewModel


//customer dashboard screen
// Updated ServiceProvider data class to include experience
data class ServiceProvider(
    val uid: String = "",
    val fullName: String = "",
    val phone: String = "",
    val experience: String = "",
    val trade: String = "",
    val address: String = ""
)

@Composable
fun ProviderListScreen(
    offering: String,
    onBack: () -> Unit
) {
    val providerViewModel: ProviderViewModel = viewModel()
    val providers by providerViewModel.providers.collectAsState()
    val loading by providerViewModel.loading.collectAsState()
    
    LaunchedEffect(offering) {
        providerViewModel.fetchProvidersByTrade(offering)
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
                    "Available $offering\nProviders",
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }
        }
        
        // Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(White)
        ) {
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryRed)
                }
            } else if (providers.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "No Technicians",
                        tint = Gray,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No technicians available right now!",
                        color = DarkGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Please check back later or try a different service.",
                        color = Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    LazyColumn {
                        items(providers) { provider ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = provider.fullName,
                                        color = DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Trade: ${provider.trade}",
                                        color = DarkGray,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Experience: ${provider.experience} years",
                                        color = DarkGray,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Address: ${provider.address}",
                                        color = Gray,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Button(
                                            onClick = { /* TODO: Handle repairing booking */ },
                                            shape = RoundedCornerShape(20),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Repairing Charges: 800", color = White, fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Button(
                                            onClick = { /* TODO: Handle replacement booking */ },
                                            shape = RoundedCornerShape(20),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Replacement: 2000", color = White, fontSize = 14.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { /* TODO: Handle general booking */ },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(25.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                                    ) {
                                        Text("Book Service", color = White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class LocalContext {
    object current {
        fun startActivity(intent: Intent) {

        }

    }

}

@Composable
fun CustomerDashboardScreen(navController: NavHostController = rememberNavController()) {
    val selectedTab = remember { mutableStateOf(0) }
    val blue = Color(0xFF1877F3)
    val lightGray = Color(0xFFF5F6FA)
    val gradientColors = listOf(Color(0xFFB31217), Color(0xFF2A0845))

    val showProviderList = remember { mutableStateOf(false) }
    val selectedService = remember { mutableStateOf("") }

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
        if (showProviderList.value) {
            ProviderListScreen(
                offering = selectedService.value,
                onBack = { showProviderList.value = false },

            )
        } else {
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
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }

                // Dynamic Screen Based on Tab
                when (selectedTab.value) {
                    0 -> {
                        // Home
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
                                Text("Quick Actions", color = Color(0xFF2A0845), fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Button(
                                        onClick = { }, modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                                    ) {
                                        Text("Book a Service", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Button(
                                        onClick = { }, modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                                    ) {
                                        Text("View Bookings", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Button(
                                        onClick = { }, modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                                    ) {
                                        Text("Browse Pros", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Button(
                                        onClick = { }, modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                                    ) {
                                        Text("Wallet", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { }, modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = lightGray)
                                ) {
                                    Text("Contact Support", color = Color(0xFF2A0845), fontWeight = FontWeight.SemiBold)
                                }

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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedService.value = "Plumber"
                                                showProviderList.value = true
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFB31217)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Build, contentDescription = "Plumber", tint = Color.White, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Plumber", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedService.value = "Electrician"
                                                showProviderList.value = true
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFF2A0845)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.ElectricalServices, contentDescription = "Electrician", tint = Color.White, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Electrician", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedService.value = "AC Guy"
                                                showProviderList.value = true
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFF1877F3)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.AcUnit, contentDescription = "AC Guy", tint = Color.White, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("AC Guy", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedService.value = "Painter"
                                                showProviderList.value = true
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFF5F6FA)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.FormatPaint, contentDescription = "Painter", tint = Color(0xFF2A0845), modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Painter", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedService.value = "Carpenter"
                                                showProviderList.value = true
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFF2A0845)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Handyman, contentDescription = "Carpenter", tint = Color.White, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Carpenter", color = Color(0xFF2A0845), fontSize = 12.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Search
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            SearchScreen(onServiceSelected = {
                                selectedService.value = it
                                showProviderList.value = true
                            })
                        }
                    }

                    2 -> {
                        // Profile
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileScreen(onLogout = { 
                                // Sign out from Firebase
                                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                                auth.signOut()
                                // Navigate to signin screen
                                navController.navigate("signin") {
                                    popUpTo(0) { inclusive = true }
                                }
                            })

                        }
                    }
                }

                // Bottom Navigation
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    NavigationBarItem(
                        selected = selectedTab.value == 0,
                        onClick = { selectedTab.value = 0 },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab.value == 1,
                        onClick = { selectedTab.value = 1 },
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                        label = { Text("Search") }
                    )
                    NavigationBarItem(
                        selected = selectedTab.value == 2,
                        onClick = { selectedTab.value = 2 },
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onServiceSelected: (String) -> Unit
) {
    val offerings = listOf("Plumber", "Electrician", "AC Guy", "Painter", "Carpenter")
    var query by remember { mutableStateOf("") }

    val filtered = offerings.filter {
        it.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search services") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search Icon")
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Color(0xFF2A0845),
                unfocusedBorderColor = Color.Gray,           // optional
                cursorColor          = Color(0xFF2A0845)
            )

        )

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isNotBlank()) {
            LazyColumn {
                items(filtered) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onServiceSelected(item) },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                color = Color(0xFF2A0845),
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Select",
                                tint = Color(0xFF2A0845),
                                modifier = Modifier.graphicsLayer(rotationZ = 180f)
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Start typing to search services...",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    
    // Fetch current user data
    LaunchedEffect(Unit) {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        name = document.getString("name") ?: ""
                        email = document.getString("email") ?: currentUser.email ?: ""
                        profileImageUrl = document.getString("profileImageUrl")
                    } else {
                        // Fallback to Firebase Auth email
                        name = currentUser.displayName ?: "User"
                        email = currentUser.email ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    // Fallback to Firebase Auth data
                    name = currentUser.displayName ?: "User"
                    email = currentUser.email ?: ""
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Image
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(LightGrayBackground)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                // Show selected image
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else if (profileImageUrl != null) {
                // Show profile image from Firebase
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Show default icon
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Image",
                    tint = PrimaryRed,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            // Camera icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Add Photo",
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            CircularProgressIndicator(color = PrimaryRed)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading profile...", color = Gray, fontSize = 14.sp)
        } else {
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkGray)
            Text(email, color = Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { showDialog = true },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Edit Profile", color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLogout() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = LightGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Log Out", color = DarkGray)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Edit Profile", fontWeight = FontWeight.Bold, color = DarkGray)
            },
            text = {
                Column {
                    // Profile Image Section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(LightGrayBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                // Show selected image
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (profileImageUrl != null) {
                                // Show profile image from Firebase
                                AsyncImage(
                                    model = profileImageUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Profile Image",
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Add Photo",
                                tint = White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Picture", color = White, fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFF2A0845),
                            unfocusedBorderColor = Color.Gray,           // optional
                            cursorColor          = Color(0xFF2A0845)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = PrimaryRed,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor          = PrimaryRed,
                            focusedTextColor     = Color.Black,
                            unfocusedTextColor   = Color.DarkGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        // Save changes to Firebase
                        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
                        val currentUser = auth.currentUser
                        
                        if (currentUser != null) {
                            // First upload image if selected
                            if (selectedImageUri != null) {
                                val imageRef = storage.reference.child("profile_images/${currentUser.uid}.jpg")
                                imageRef.putFile(selectedImageUri!!)
                                    .addOnSuccessListener { taskSnapshot ->
                                        // Get download URL
                                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                            // Update Firestore with image URL and other data
                                            val updates = hashMapOf<String, Any>(
                                                "name" to name,
                                                "email" to email,
                                                "profileImageUrl" to downloadUri.toString()
                                            )
                                            firestore.collection("users").document(currentUser.uid)
                                                .update(updates as Map<String, Any>)
                                                .addOnSuccessListener {
                                                    profileImageUrl = downloadUri.toString()
                                                    selectedImageUri = null
                                                    showDialog = false
                                                }
                                                .addOnFailureListener {
                                                    showDialog = false
                                                }
                                        }
                                    }
                                    .addOnFailureListener {
                                        // If image upload fails, still save other data
                                        val updates = hashMapOf<String, Any>(
                                            "name" to name,
                                            "email" to email
                                        )
                                        firestore.collection("users").document(currentUser.uid)
                                            .update(updates as Map<String, Any>)
                                            .addOnSuccessListener {
                                                showDialog = false
                                            }
                                            .addOnFailureListener {
                                                showDialog = false
                                            }
                                    }
                            } else {
                                // No image selected, just update text data
                                val updates = hashMapOf<String, Any>(
                                    "name" to name,
                                    "email" to email
                                )
                                firestore.collection("users").document(currentUser.uid)
                                    .update(updates as Map<String, Any>)
                                    .addOnSuccessListener {
                                        showDialog = false
                                    }
                                    .addOnFailureListener {
                                        showDialog = false
                                    }
                            }
                        } else {
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text("Save", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = PrimaryRed)
                }
            }
        )
    }
}
