package com.example.skilllink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun SignUpScreen(navController: NavHostController, onSignUp: () -> Unit = {}, onSignIn: () -> Unit = {}) {
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }
    val roles = listOf("Customer", "Skilled Person")
    val selectedRole = remember { mutableStateOf(roles[0]) }
    val showValidationError = remember { mutableStateOf(false) }
    val isFormValid = name.value.isNotBlank() && email.value.isNotBlank() && password.value.isNotBlank() && confirmPassword.value.isNotBlank() && password.value == confirmPassword.value

    LaunchedEffect(authState) {
        if (authState is AuthResult.Success) {
            snackbarHostState.showSnackbar("Account created successfully!")
            if (selectedRole.value == "Customer") {
                navController.navigate(Screen.CustomerDashboard.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.SkilledDashboard.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            }
            viewModel.resetState()
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
                    text = "Create Your\nAccount",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                IconButton(onClick = { /* TODO: menu */ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.White)
                }
            }
            // Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f)
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
                    Text("Select your role:", color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.Start))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        roles.forEach { role ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                RadioButton(
                                    selected = selectedRole.value == role,
                                    onClick = { selectedRole.value = role }
                                )
                                Text(role, color = Color.DarkGray)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text("Full Name") },
                        trailingIcon = {
                            if (name.value.isNotEmpty())
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFFB31217))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Phone or Gmail") },
                        trailingIcon = {
                            if (email.value.isNotEmpty())
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFFB31217))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                    OutlinedTextField(
                        value = confirmPassword.value,
                        onValueChange = { confirmPassword.value = it },
                        label = { Text("Confirm Password") },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }) {
                                Icon(Icons.Filled.Visibility, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isFormValid) {
                                viewModel.signUp(
                                    name = name.value,
                                    email = email.value,
                                    password = password.value,
                                    role = selectedRole.value,
                                    onSuccess = onSignUp,
                                    onError = {}
                                )
                            } else {
                                showValidationError.value = true
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
                            Text("SIGN UP", color = Color.White, fontSize = 18.sp)
                        }
                    }
                    if (showValidationError.value && !isFormValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val errorMsg = when {
                            name.value.isBlank() -> "Name is required."
                            email.value.isBlank() -> "Email is required."
                            password.value.isBlank() -> "Password is required."
                            confirmPassword.value.isBlank() -> "Confirm password is required."
                            password.value != confirmPassword.value -> "Passwords do not match."
                            else -> "Please fill all fields."
                        }
                        Text(errorMsg, color = Color.Red, fontSize = 14.sp)
                    }
                    if (authState is AuthResult.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text((authState as AuthResult.Error).message, color = Color.Red, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have account? ", color = Color.Gray)
                        TextButton(onClick = onSignIn) {
                            Text("Sign In", color = Color(0xFFB31217))
                        }
                    }
                }
            }
        }
    }
} 