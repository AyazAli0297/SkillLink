package com.example.skilllink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skilllink.ui.components.toBrushColor
import com.example.skilllink.viewmodel.AuthViewModel
import com.example.skilllink.viewmodel.AuthResult
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.skilllink.navigation.Screen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SignInScreen(
    navController: NavHostController,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val isFormValid = email.value.isNotBlank() && password.value.isNotBlank()
    val snackbarMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        if (authState is AuthResult.Error) {
            snackbarMessage.value = (authState as AuthResult.Error).message
            viewModel.resetState()
        }
    }

    // Navigation on success
    val lastRole = remember { mutableStateOf("") }
    LaunchedEffect(authState, lastRole.value) {
        if (authState is AuthResult.Success && lastRole.value.isNotBlank()) {
            if (lastRole.value == "customer") {
                navController.navigate(Screen.CustomerDashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            } else if (lastRole.value == "provider") {
                navController.navigate(Screen.SkilledDashboard.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
            viewModel.resetState()
            lastRole.value = ""
        }
    }

    // Show snackbar if message is set
    LaunchedEffect(snackbarMessage.value) {
        snackbarMessage.value?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage.value = null
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
                        colors = listOf(Color(0xFFB31217), Color(0xFF2A0845))
                    )
                )
                .padding(innerPadding)
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello\nSign in!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                // Menu dropdown
                val showMenu = remember { mutableStateOf(false) }

                Box {
                    IconButton(onClick = { showMenu.value = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Help") },
                            onClick = {
                                showMenu.value = false
                                snackbarMessage.value = "Help option selected"
                            },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu.value = false
                                snackbarMessage.value = "About option selected"
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Info, contentDescription = null)
                            }
                        )
                    }
                }
            }
            // Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Gmail") },
                        trailingIcon = {
                            if (email.value.isNotEmpty())
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFFB31217))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                                Icon(Icons.Filled.Visibility, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        // Forgot password dialog state
                        val showForgotPasswordDialog = remember { mutableStateOf(false) }
                        val resetEmail = remember { mutableStateOf("") }

                        TextButton(onClick = { 
                            showForgotPasswordDialog.value = true
                            resetEmail.value = email.value
                        }) {
                            Text("Forgot password?", color = Color.Gray)
                        }

                        // Forgot password dialog
                        if (showForgotPasswordDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showForgotPasswordDialog.value = false },
                                title = { Text("Reset Password") },
                                text = {
                                    Column {
                                        Text("Enter your email address to receive a password reset link.")
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                            value = resetEmail.value,
                                            onValueChange = { resetEmail.value = it },
                                            label = { Text("Email") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (resetEmail.value.isNotBlank()) {
                                                // Send password reset email
                                                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                                                auth.sendPasswordResetEmail(resetEmail.value)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            snackbarMessage.value = "Password reset email sent to ${resetEmail.value}"
                                                        } else {
                                                            snackbarMessage.value = "Failed to send reset email: ${task.exception?.message}"
                                                        }
                                                        showForgotPasswordDialog.value = false
                                                    }
                                            } else {
                                                snackbarMessage.value = "Please enter a valid email address"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Brush.horizontalGradient(
                                                listOf(Color(0xFFB31217), Color(0xFF2A0845))
                                            ).toBrushColor()
                                        )
                                    ) {
                                        Text("Reset Password", color = Color.White)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showForgotPasswordDialog.value = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isFormValid) {
                                viewModel.signIn(
                                    email = email.value,
                                    password = password.value,
                                    onSuccess = { role -> lastRole.value = role },
                                    onError = { /* handled by authState */ }
                                )
                            } else {
                                snackbarMessage.value = "Please enter email and password."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brush.horizontalGradient(listOf(Color(0xFFB31217), Color(0xFF2A0845))).toBrushColor()),
                        enabled = isFormValid && authState !is AuthResult.Loading
                    ) {
                        if (authState is AuthResult.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("SIGN IN", color = Color.White, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account? ", 
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        TextButton(
                            onClick = onSignUp,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                "Sign Up", 
                                color = Color(0xFFB31217),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
} 
