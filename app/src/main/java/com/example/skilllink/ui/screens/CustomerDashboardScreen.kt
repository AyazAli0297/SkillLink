package com.example.skilllink.ui.screens

import android.content.Intent
import android.net.Uri

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.Help
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.skilllink.data.models.*


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

// Data class for customer bookings
data class CustomerBooking(
    val id: String = "",
    val skilledName: String = "",
    val service: String = "",
    val status: String = "Pending", // Pending, Accepted, Rejected, Completed
    val price: String = "",
    val address: String = "",
    val date: String = "",
    val time: String = "",
    val skilledPhone: String = "",
    val trade: String = "",
    val paymentStatus: String = "Unpaid", // Unpaid, Paid, Refunded
    val transactionId: String = "",
    val paymentMethod: String = "",
    val amount: Double = 0.0
)

@Composable
fun ProviderListScreen(
    offering: String,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit = {},
    navController: NavHostController? = null,
    paymentAmount: MutableState<Double>,
    paymentService: MutableState<String>,
    paymentProviderName: MutableState<String>,
    paymentBookingData: MutableState<Map<String, Any>>,
    showPaymentScreen: MutableState<Boolean>
) {
    val providerViewModel: ProviderViewModel = viewModel()
    val providers by providerViewModel.providers.collectAsState()
    val loading by providerViewModel.loading.collectAsState()
    val context = LocalContext.current

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
                    val context = LocalContext.current
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
                                            onClick = { 
                                                // Launch payment for repairing service
                                                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                                                val currentUser = auth.currentUser

                                                if (currentUser != null) {
                                                    // Set payment data
                                                    paymentAmount.value = 800.0
                                                    paymentService.value = "Repairing Service"
                                                    paymentProviderName.value = provider.fullName
                                                    paymentBookingData.value = mapOf(
                                                        "customerId" to currentUser.uid,
                                                        "customerName" to (currentUser.displayName ?: "Customer"),
                                                        "customerPhone" to "",
                                                        "skilledId" to provider.uid,
                                                        "skilledName" to provider.fullName,
                                                        "skilledPhone" to provider.phone,
                                                        "service" to "Repairing Service",
                                                        "status" to "Pending",
                                                        "date" to java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                                                        "time" to java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                                                        "address" to provider.address,
                                                        "trade" to provider.trade
                                                    )
                                                    // Show payment screen
                                                    showPaymentScreen.value = true
                                                }
                                            },
                                            shape = RoundedCornerShape(20),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Repairing Charges: 800", color = White, fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Button(
                                            onClick = { 
                                                // Launch payment for replacement service
                                                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                                                val currentUser = auth.currentUser

                                                if (currentUser != null) {
                                                    // Set payment data
                                                    paymentAmount.value = 2000.0
                                                    paymentService.value = "Replacement Service"
                                                    paymentProviderName.value = provider.fullName
                                                    paymentBookingData.value = mapOf(
                                                        "customerId" to currentUser.uid,
                                                        "customerName" to (currentUser.displayName ?: "Customer"),
                                                        "customerPhone" to "",
                                                        "skilledId" to provider.uid,
                                                        "skilledName" to provider.fullName,
                                                        "skilledPhone" to provider.phone,
                                                        "service" to "Replacement Service",
                                                        "status" to "Pending",
                                                        "date" to java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                                                        "time" to java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                                                        "address" to provider.address,
                                                        "trade" to provider.trade
                                                    )
                                                    // Show payment screen
                                                    showPaymentScreen.value = true
                                                }
                                            },
                                            shape = RoundedCornerShape(20),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Replacement: 2000", color = White, fontSize = 14.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { 
                                            // Launch payment for general service
                                            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                                            val currentUser = auth.currentUser

                                            if (currentUser != null) {
                                                // Set payment data
                                                paymentAmount.value = 1000.0
                                                paymentService.value = "General ${provider.trade} Service"
                                                paymentProviderName.value = provider.fullName
                                                paymentBookingData.value = mapOf(
                                                    "customerId" to currentUser.uid,
                                                    "customerName" to (currentUser.displayName ?: "Customer"),
                                                    "customerPhone" to "",
                                                    "skilledId" to provider.uid,
                                                    "skilledName" to provider.fullName,
                                                    "skilledPhone" to provider.phone,
                                                    "service" to "General ${provider.trade} Service",
                                                    "status" to "Pending",
                                                    "date" to java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                                                    "time" to java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                                                    "address" to provider.address,
                                                    "trade" to provider.trade
                                                )
                                                // Show payment screen
                                                showPaymentScreen.value = true
                                            }
                                        },
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


@Composable
fun CustomerDashboardScreen(navController: NavHostController = rememberNavController()) {
    val selectedTab = remember { mutableStateOf(0) }
    val blue = Color(0xFF1877F3)
    val lightGray = Color(0xFFF5F6FA)
    val gradientColors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
    val context = LocalContext.current

    val showProviderList = remember { mutableStateOf(false) }
    val selectedService = remember { mutableStateOf("") }
    
    // Payment state
    val showPaymentScreen = remember { mutableStateOf(false) }
    val paymentAmount = remember { mutableStateOf(0.0) }
    val paymentService = remember { mutableStateOf("") }
    val paymentProviderName = remember { mutableStateOf("") }
    val paymentBookingData = remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    
    // Wallet and transaction state
    val showWalletScreen = remember { mutableStateOf(false) }
    val showTransactionHistory = remember { mutableStateOf(false) }

    // Customer bookings state
    var customerBookings by remember { mutableStateOf<List<CustomerBooking>>(emptyList()) }
    var isLoadingBookings by remember { mutableStateOf(true) }
    var refreshBookings by remember { mutableStateOf(0) }

    // Function to fetch bookings
    fun fetchCustomerBookings() {
        isLoadingBookings = true
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("bookings")
                .whereEqualTo("customerId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val fetchedBookings = mutableListOf<CustomerBooking>()
                    for (document in documents) {
                        val booking = CustomerBooking(
                            id = document.id,
                            skilledName = document.getString("skilledName") ?: "",
                            service = document.getString("service") ?: "",
                            status = document.getString("status") ?: "Pending",
                            price = document.getString("price") ?: "",
                            address = document.getString("address") ?: "",
                            date = document.getString("date") ?: "",
                            time = document.getString("time") ?: "",
                            skilledPhone = document.getString("skilledPhone") ?: "",
                            trade = document.getString("trade") ?: "",
                            paymentStatus = document.getString("paymentStatus") ?: "Unpaid",
                            transactionId = document.getString("transactionId") ?: "",
                            paymentMethod = document.getString("paymentMethod") ?: "",
                            amount = document.getDouble("amount") ?: 0.0
                        )
                        fetchedBookings.add(booking)
                    }
                    customerBookings = fetchedBookings
                    isLoadingBookings = false
                }
                .addOnFailureListener {
                    customerBookings = emptyList()
                    isLoadingBookings = false
                }
        } else {
            isLoadingBookings = false
        }
    }

    // Fetch customer bookings from Firestore
    LaunchedEffect(refreshBookings) {
        fetchCustomerBookings()
    }

    // Handle back button navigation
    BackHandler {
        when {
            showTransactionHistory.value -> {
                showTransactionHistory.value = false
            }
            showWalletScreen.value -> {
                showWalletScreen.value = false
            }
            showPaymentScreen.value -> {
                showPaymentScreen.value = false
            }
            showProviderList.value -> {
                showProviderList.value = false
            }
            selectedTab.value == 3 -> {
                selectedTab.value = 0
            }
            selectedTab.value != 0 -> {
                selectedTab.value = 0
            }
            else -> {
                // If we're on the main home tab, let the system handle the back press (exit app)
                // This is the default behavior when BackHandler is not handled
            }
        }
    }

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
        if (showTransactionHistory.value) {
            TransactionHistoryScreen(
                onBack = { showTransactionHistory.value = false }
            )
        } else if (showWalletScreen.value) {
            WalletScreen(
                onBack = { showWalletScreen.value = false },
                onTransactionHistory = { 
                    showWalletScreen.value = false
                    showTransactionHistory.value = true 
                }
            )
        } else if (showPaymentScreen.value) {
            PaymentScreen(
                amount = paymentAmount.value,
                service = paymentService.value,
                providerName = paymentProviderName.value,
                bookingData = paymentBookingData.value,
                onBack = { showPaymentScreen.value = false },
                onPaymentSuccess = { 
                    showPaymentScreen.value = false
                    refreshBookings++ // Refresh bookings when payment is successful
                    android.widget.Toast.makeText(
                        context,
                        "Payment successful! Booking confirmed.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                },
                onPaymentFailed = { error ->
                    showPaymentScreen.value = false
                    android.widget.Toast.makeText(
                        context,
                        "Payment failed: $error",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            )
        } else if (showProviderList.value) {
            ProviderListScreen(
                offering = selectedService.value,
                onBack = { showProviderList.value = false },
                onBookingSuccess = { 
                    refreshBookings++ // Refresh bookings when a new booking is made
                },
                paymentAmount = paymentAmount,
                paymentService = paymentService,
                paymentProviderName = paymentProviderName,
                paymentBookingData = paymentBookingData,
                showPaymentScreen = showPaymentScreen
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
                    // Settings menu
                    var showSettingsMenu by remember { mutableStateOf(false) }

                    Box {
                        IconButton(onClick = { showSettingsMenu = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                        }

                        DropdownMenu(
                            expanded = showSettingsMenu,
                            onDismissRequest = { showSettingsMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("App Settings") },
                                onClick = {
                                    showSettingsMenu = false
                                    android.widget.Toast.makeText(
                                        context,
                                        "App Settings clicked",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Settings, contentDescription = null)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Help & Support") },
                                onClick = {
                                    showSettingsMenu = false
                                    android.widget.Toast.makeText(
                                        context,
                                        "Help & Support clicked",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("About") },
                                onClick = {
                                    showSettingsMenu = false
                                    android.widget.Toast.makeText(
                                        context,
                                        "About clicked",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Info, contentDescription = null)
                                }
                            )
                        }
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
                                        onClick = { 
                                            // Navigate to search tab
                                            selectedTab.value = 1
                                        }, 
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                                    ) {
                                        Text("Book a Service", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Button(
                                        onClick = { 
                                            // Navigate to a dedicated bookings view (using tab 3)
                                            selectedTab.value = 3
                                        }, 
                                        modifier = Modifier.weight(1f).height(48.dp),
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
                                        onClick = { 
                                            showWalletScreen.value = true
                                        }, modifier = Modifier.weight(1f).height(48.dp),
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

                                // Recent Activity Section
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Recent Activity",
                                    color = Color(0xFF2A0845),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                if (isLoadingBookings) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color(0xFFB31217))
                                    }
                                } else if (customerBookings.isEmpty()) {
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
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 300.dp) // Limit height to prevent layout issues
                                    ) {
                                        items(customerBookings.take(2)) { booking ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                shape = RoundedCornerShape(16.dp),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            booking.service,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF2A0845),
                                                            modifier = Modifier.weight(1f)
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
                                                        "Provider: ${booking.skilledName}",
                                                        fontSize = 14.sp,
                                                        color = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "Trade: ${booking.trade}",
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
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF2A0845)
                                                    )

                                                    if (booking.status == "Accepted") {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Button(
                                                            onClick = {
                                                                // Contact provider logic
                                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                                    data = Uri.parse("tel:${booking.skilledPhone}")
                                                                }
                                                                context.startActivity(intent)
                                                            },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F3))
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.Phone,
                                                                contentDescription = "Call",
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Contact Provider")
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
                                navController.navigate(com.example.skilllink.navigation.Screen.SignIn.route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            })

                        }
                    }

                    3 -> {
                        // Bookings Screen
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                                .background(Color.White)
                        ) {
                            BookingsScreen(
                                bookings = customerBookings,
                                isLoading = isLoadingBookings,
                                onRefresh = { refreshBookings++ },
                                onBack = { selectedTab.value = 0 },
                                context = context
                            )
                        }
                    }
                }

                // Bottom Navigation
                if (selectedTab.value != 3) { // Hide bottom navigation when in bookings screen
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
                                                "fullName" to name, // Keep both for compatibility
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
                                            "fullName" to name, // Keep both for compatibility
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
                                    "fullName" to name, // Keep both for compatibility
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

@Composable
fun BookingsScreen(
    bookings: List<CustomerBooking>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    context: android.content.Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2A0845))
                }
                Text(
                    "My Bookings",
                    color = Color(0xFF2A0845),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onRefresh) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = Color(0xFF2A0845))
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFB31217))
            }
        } else if (bookings.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.BookmarkBorder,
                    contentDescription = "No Bookings",
                    tint = Color.Gray,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No bookings yet",
                    color = Color(0xFF2A0845),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your bookings will appear here once you book a service",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(bookings) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    booking.service,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2A0845),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    booking.status,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when(booking.status) {
                                        "Pending" -> Color(0xFFFFA500)
                                        "Accepted" -> Color(0xFF4CAF50)
                                        "Completed" -> Color(0xFF2196F3)
                                        "Rejected" -> Color(0xFFFF5722)
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier
                                        .background(
                                            when(booking.status) {
                                                "Pending" -> Color(0xFFFFA500).copy(alpha = 0.1f)
                                                "Accepted" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                                "Completed" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                                                "Rejected" -> Color(0xFFFF5722).copy(alpha = 0.1f)
                                                else -> Color.Gray.copy(alpha = 0.1f)
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Provider: ${booking.skilledName}",
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Trade: ${booking.trade}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Date: ${booking.date} at ${booking.time}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Text(
                                    booking.price,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2A0845)
                                )
                            }
                            
                            if (booking.status == "Accepted") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${booking.skilledPhone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F3))
                                ) {
                                    Icon(
                                        Icons.Filled.Phone,
                                        contentDescription = "Call",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Contact Provider")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
