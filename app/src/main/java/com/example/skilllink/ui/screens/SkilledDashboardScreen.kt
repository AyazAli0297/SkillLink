package com.example.skilllink.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.skilllink.navigation.Screen
import com.example.skilllink.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// Data model for a booking request
data class BookingRequest(
    val id: String = "",
    val customerName: String = "",
    val service: String = "",
    val status: String = "Pending", // Pending, Accepted, Rejected, Completed
    val price: String = "",
    val address: String = "",
    val date: String = "",
    val time: String = "",
    val customerPhone: String = ""
)

@Composable
fun SkilledDashboardScreen(navController: NavHostController = rememberNavController()) {
    val selectedTab = remember { mutableStateOf(0) }
    val gradientColors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // User data
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Booking requests
    var bookingRequests by remember { mutableStateOf<List<BookingRequest>>(emptyList()) }
    var isLoadingBookings by remember { mutableStateOf(true) }

    // Fetch user data
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userData = document.data
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }

            // Fetch booking requests (this would be replaced with actual implementation)
            // In a real app, you'd filter by the skilled person's ID and trade
            bookingRequests = listOf(
                BookingRequest(
                    id = "1",
                    customerName = "John Doe",
                    service = "Plumbing Repair",
                    status = "Pending",
                    price = "Rs. 800",
                    address = "123 Main St, Islamabad",
                    date = "2023-06-15",
                    time = "14:00",
                    customerPhone = "+92 300 1234567"
                ),
                BookingRequest(
                    id = "2",
                    customerName = "Jane Smith",
                    service = "Pipe Installation",
                    status = "Accepted",
                    price = "Rs. 1200",
                    address = "456 Park Ave, Lahore",
                    date = "2023-06-16",
                    time = "10:00",
                    customerPhone = "+92 300 7654321"
                ),
                BookingRequest(
                    id = "3",
                    customerName = "Ahmed Khan",
                    service = "Sink Repair",
                    status = "Completed",
                    price = "Rs. 500",
                    address = "789 Garden Rd, Karachi",
                    date = "2023-06-14",
                    time = "16:00",
                    customerPhone = "+92 300 9876543"
                )
            )
            isLoadingBookings = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
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
                .padding(innerPadding)
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
                        text = "Skilled Dashboard",
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
                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFFB31217))
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 32.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    // Welcome message with user's name
                                    Text(
                                        "Welcome, ${userData?.get("fullName") ?: "Skilled Professional"}!",
                                        color = Color(0xFF2A0845),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    // Professional info card
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                "Professional Information",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2A0845)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row {
                                                Text(
                                                    "Trade: ",
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    "${userData?.get("trade") ?: "Not specified"}",
                                                    color = Color.Black
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row {
                                                Text(
                                                    "Experience: ",
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    "${userData?.get("experience") ?: "0"} years",
                                                    color = Color.Black
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row {
                                                Text(
                                                    "Phone: ",
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    "${userData?.get("phone") ?: "Not provided"}",
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Quick Actions",
                                        color = Color(0xFF2A0845),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    // Quick action buttons
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Button(
                                            onClick = { selectedTab.value = 1 },
                                            modifier = Modifier.weight(1f).height(48.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB31217))
                                        ) {
                                            Text("View Bookings", color = Color.White, fontWeight = FontWeight.SemiBold)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Button(
                                            onClick = { selectedTab.value = 2 },
                                            modifier = Modifier.weight(1f).height(48.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A0845))
                                        ) {
                                            Text("Edit Profile", color = Color.White, fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { /* TODO: Set availability */ },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F3))
                                    ) {
                                        Text("Set Availability", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        "Recent Activity",
                                        color = Color(0xFF2A0845),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // Recent activity cards
                                    if (bookingRequests.isEmpty()) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            elevation = CardDefaults.cardElevation(4.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Info,
                                                    contentDescription = "No Activity",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    "No recent activity",
                                                    color = Color.Gray,
                                                    fontSize = 16.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    } else {
                                        // Show the most recent 2 bookings
                                        bookingRequests.take(2).forEach { booking ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                shape = RoundedCornerShape(16.dp),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            booking.service,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF2A0845)
                                                        )
                                                        Text(
                                                            booking.status,
                                                            fontSize = 14.sp,
                                                            color = when(booking.status) {
                                                                "Pending" -> Color(0xFFFFA500)
                                                                "Accepted" -> Color(0xFF4CAF50)
                                                                "Completed" -> Color(0xFF2196F3)
                                                                "Rejected" -> Color(0xFFFF5722)
                                                                else -> Color.Gray
                                                            }
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "Customer: ${booking.customerName}",
                                                        fontSize = 14.sp,
                                                        color = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "Date: ${booking.date} at ${booking.time}",
                                                        fontSize = 14.sp,
                                                        color = Color.Gray
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "Price: ${booking.price}",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF2A0845)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Bookings
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White)
                        ) {
                            if (isLoadingBookings) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFFB31217))
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 32.dp)
                                ) {
                                    Text(
                                        "Booking Requests",
                                        color = Color(0xFF2A0845),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    if (bookingRequests.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Filled.Info,
                                                    contentDescription = "No Bookings",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    "No booking requests yet",
                                                    color = Color.Gray,
                                                    fontSize = 16.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    } else {
                                        LazyColumn {
                                            items(bookingRequests) { booking ->
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 8.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    elevation = CardDefaults.cardElevation(4.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                booking.service,
                                                                fontSize = 18.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF2A0845)
                                                            )
                                                            Text(
                                                                booking.status,
                                                                fontSize = 14.sp,
                                                                color = when(booking.status) {
                                                                    "Pending" -> Color(0xFFFFA500)
                                                                    "Accepted" -> Color(0xFF4CAF50)
                                                                    "Completed" -> Color(0xFF2196F3)
                                                                    "Rejected" -> Color(0xFFFF5722)
                                                                    else -> Color.Gray
                                                                }
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            "Customer: ${booking.customerName}",
                                                            fontSize = 16.sp,
                                                            color = Color.Black
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            "Phone: ${booking.customerPhone}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            "Address: ${booking.address}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            "Date: ${booking.date} at ${booking.time}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            "Price: ${booking.price}",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = Color(0xFF2A0845)
                                                        )

                                                        // Action buttons based on status
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                        when (booking.status) {
                                                            "Pending" -> {
                                                                Row(modifier = Modifier.fillMaxWidth()) {
                                                                    Button(
                                                                        onClick = {
                                                                            // TODO: Accept booking logic
                                                                            coroutineScope.launch {
                                                                                snackbarHostState.showSnackbar("Booking accepted")
                                                                            }
                                                                        },
                                                                        modifier = Modifier.weight(1f),
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                                                    ) {
                                                                        Text("Accept", color = Color.White)
                                                                    }
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Button(
                                                                        onClick = {
                                                                            // TODO: Reject booking logic
                                                                            coroutineScope.launch {
                                                                                snackbarHostState.showSnackbar("Booking rejected")
                                                                            }
                                                                        },
                                                                        modifier = Modifier.weight(1f),
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                                                                    ) {
                                                                        Text("Reject", color = Color.White)
                                                                    }
                                                                }
                                                            }
                                                            "Accepted" -> {
                                                                Button(
                                                                    onClick = {
                                                                        // TODO: Mark as completed logic
                                                                        coroutineScope.launch {
                                                                            snackbarHostState.showSnackbar("Booking marked as completed")
                                                                        }
                                                                    },
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                                                                ) {
                                                                    Text("Mark as Completed", color = Color.White)
                                                                }
                                                            }
                                                            "Completed" -> {
                                                                Text(
                                                                    "This booking has been completed",
                                                                    color = Color(0xFF4CAF50),
                                                                    fontSize = 14.sp,
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.fillMaxWidth()
                                                                )
                                                            }
                                                            "Rejected" -> {
                                                                Text(
                                                                    "This booking was rejected",
                                                                    color = Color(0xFFFF5722),
                                                                    fontSize = 14.sp,
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.fillMaxWidth()
                                                                )
                                                            }
                                                        }

                                                        // Contact customer button
                                                        if (booking.status != "Rejected") {
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                            val context = LocalContext.current
                                                            OutlinedButton(
                                                                onClick = {
                                                                    // Contact customer logic
                                                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                                                        data = Uri.parse("tel:${booking.customerPhone}")
                                                                    }
                                                                    context.startActivity(intent)
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2A0845))
                                                            ) {
                                                                Icon(
                                                                    Icons.Filled.Phone,
                                                                    contentDescription = "Call",
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text("Contact Customer")
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
                    }

                    2 -> {
                        // Profile
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White)
                        ) {
                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFFB31217))
                                }
                            } else {
                                SkilledProfileTab(
                                    userData = userData,
                                    onLogout = {
                                        // Sign out from Firebase
                                        val auth = FirebaseAuth.getInstance()
                                        auth.signOut()
                                        // Navigate to signin screen
                                        navController.navigate(Screen.SignIn.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    onEditProfile = {
                                        // Navigate to profile edit screen
                                        navController.navigate(Screen.SkilledProfile.route)
                                    }
                                )
                            }
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
                        icon = { Icon(Icons.Filled.Book, contentDescription = "Bookings") },
                        label = { Text("Bookings") }
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

@Composable
fun SkilledProfileTab(
    userData: Map<String, Any>?,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
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
                .clip(CircleShape)
                .background(Color(0xFFF5F6FA)),
            contentAlignment = Alignment.Center
        ) {
            val profileImageUrl = userData?.get("profileImageUrl") as? String
            if (profileImageUrl != null) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Image",
                    tint = Color(0xFFB31217),
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            userData?.get("fullName") as? String ?: "Skilled Professional",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2A0845)
        )
        Text(
            "Trade: ${userData?.get("trade") as? String ?: "Not specified"}",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile details card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Personal Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A0845),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Phone", color = Color.Gray)
                    Text(userData?.get("phone") as? String ?: "Not provided", color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("CNIC", color = Color.Gray)
                    Text(userData?.get("cnic") as? String ?: "Not provided", color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Experience", color = Color.Gray)
                    Text("${userData?.get("experience") as? String ?: "0"} years", color = Color.Black)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Address", color = Color.Gray)
                    Text(
                        userData?.get("address") as? String ?: "Not provided",
                        color = Color.Black,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onEditProfile,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB31217)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Edit Profile", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F6FA)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Log Out", color = Color(0xFF2A0845))
        }
    }
}
