package com.example.skilllink.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.skilllink.navigation.Screen
import com.example.skilllink.ui.components.toBrushColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SkilledProfileScreen(
    navController: NavHostController,
    onProfileComplete: () -> Unit = {},
    userId: String? = FirebaseAuth.getInstance().currentUser?.uid
) {
    val context = LocalContext.current
    val firestore = remember { FirebaseFirestore.getInstance() }
    val storage = remember { FirebaseStorage.getInstance() }
    val snackbarHostState = remember { SnackbarHostState() }

    val fullName = remember { mutableStateOf("") }
    val cnic = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val trade = remember { mutableStateOf("") }
    val experience = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val showValidationError = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val showInputErrorSnackbar = remember { mutableStateOf(false) }
    val inputErrorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val isEditMode = remember { mutableStateOf(false) } // Track if we're editing existing data

    val isFormValid = fullName.value.isNotBlank() && cnic.value.isNotBlank() && phone.value.isNotBlank() &&
            trade.value.isNotBlank() && experience.value.isNotBlank() && address.value.isNotBlank()

    // Fetch existing user data when screen loads
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val existingName = document.getString("fullName") ?: document.getString("name") ?: ""
                    val existingCnic = document.getString("cnic") ?: ""
                    val existingPhone = document.getString("phone") ?: document.getString("phoneNumber") ?: ""
                    val existingTrade = document.getString("trade") ?: ""
                    val existingExperience = document.getString("experience") ?: document.getString("yearsOfExperience") ?: ""
                    val existingAddress = document.getString("address") ?: ""
                    
                    fullName.value = existingName
                    cnic.value = existingCnic
                    phone.value = existingPhone
                    trade.value = existingTrade
                    experience.value = existingExperience
                    address.value = existingAddress
                    
                    // Set edit mode if any data exists
                    isEditMode.value = existingName.isNotBlank() || existingCnic.isNotBlank() || 
                                     existingPhone.isNotBlank() || existingTrade.isNotBlank() ||
                                     existingExperience.isNotBlank() || existingAddress.isNotBlank()
                }
            } catch (e: Exception) {
                // Handle error silently or show a message
            }
        }
    }

    suspend fun saveProfile(): Boolean {
        return try {
            val profileData = hashMapOf(
                "uid" to userId,
                "fullName" to fullName.value,
                "name" to fullName.value, // Keep both for compatibility
                "cnic" to cnic.value,
                "phone" to phone.value,
                "phoneNumber" to phone.value, // Keep both for compatibility
                "trade" to trade.value,
                "experience" to experience.value,
                "yearsOfExperience" to experience.value, // Keep both for compatibility
                "address" to address.value,
                "profileComplete" to true,
                "role" to "provider"
            )
            firestore.collection("users")
                .document(userId!!)
                .set(profileData as Map<String, Any>, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    if (showSuccessDialog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Profile Completed") },
            text = { Text("Your profile has been saved successfully! You will be redirected to your dashboard.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog.value = false
                    onProfileComplete()
                }) {
                    Text("OK")
                }
            }
        )
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
                        colors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
                    )
                )
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditMode.value) "Edit Your\nProfile" else "Complete Your\nProfile",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                IconButton(onClick = { /* TODO: menu */ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.White)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = fullName.value,
                        onValueChange = { fullName.value = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cnic.value,
                        onValueChange = { cnic.value = it },
                        label = { Text("CNIC") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone.value,
                        onValueChange = { phone.value = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = trade.value,
                        onValueChange = { trade.value = it },
                        label = { Text("Trade (e.g., Plumber, Electrician)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = experience.value,
                        onValueChange = { experience.value = it },
                        label = { Text("Years of Experience") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address.value,
                        onValueChange = { address.value = it },
                        label = { Text("Address") },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (!isFormValid || userId == null) {
                                showValidationError.value = true
                                inputErrorMessage.value = "Please fill all required fields."
                                showInputErrorSnackbar.value = true
                                return@Button
                            }
                            isLoading.value = true
                            coroutineScope.launch {
                                val success = saveProfile()
                                isLoading.value = false
                                if (success) {
                                    snackbarHostState.showSnackbar("Profile saved successfully!")
                                    navController.navigate(Screen.SkilledDashboard.route) {
                                        popUpTo(Screen.SkilledProfile.route) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("Failed to save profile. Please try again.")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brush.horizontalGradient(
                                listOf(Color(0xFFB31217), Color(0xFF2A0845))
                            ).toBrushColor()
                        ),
                        enabled = isFormValid && !isLoading.value
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                if (isEditMode.value) "Update Profile" else "Save Profile", 
                                color = Color.White, 
                                fontSize = 18.sp
                            )
                        }
                    }
                    if (showValidationError.value && !isFormValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Please fill all required fields.", color = Color.Red, fontSize = 14.sp)
                    }
                }
            }
        }
    }
    if (showInputErrorSnackbar.value) {
        LaunchedEffect(showInputErrorSnackbar.value) {
            snackbarHostState.showSnackbar(inputErrorMessage.value)
            showInputErrorSnackbar.value = false
        }
    }
}