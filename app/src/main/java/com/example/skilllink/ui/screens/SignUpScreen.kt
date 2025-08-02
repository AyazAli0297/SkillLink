package com.example.skilllink.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.skilllink.ui.components.toBrushColor
import com.example.skilllink.viewmodel.AuthViewModel
import com.example.skilllink.viewmodel.AuthResult
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.skilllink.navigation.Screen
import androidx.compose.material3.SnackbarHostState
import com.example.skilllink.ui.theme.*

@Composable
fun SignUpScreen(navController: NavHostController, onSignUp: () -> Unit = {}, onSignIn: () -> Unit = {}) {
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Basic fields
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }
    
    // Role selection
    val selectedRole = remember { mutableStateOf("Customer") }
    
    // Skilled person additional fields
    val cnic = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val selectedTrade = remember { mutableStateOf("") }
    val yearsOfExperience = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val showTradeDropdown = remember { mutableStateOf(false) }
    
    val trades = listOf(
        "Plumber", "Electrician", "AC Guy", "Painter","Carpenter"
    )
    
    val showValidationError = remember { mutableStateOf(false) }
    
    val isBasicFormValid = name.value.isNotBlank() && 
                          email.value.isNotBlank() && 
                          password.value.isNotBlank() && 
                          confirmPassword.value.isNotBlank() && 
                          password.value == confirmPassword.value
    
    val isSkilledFormValid = if (selectedRole.value == "Skilled Person") {
        isBasicFormValid && 
        cnic.value.isNotBlank() && 
        phoneNumber.value.isNotBlank() && 
        selectedTrade.value.isNotBlank() && 
        yearsOfExperience.value.isNotBlank() && 
        address.value.isNotBlank()
    } else {
        isBasicFormValid
    }

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
                        colors = listOf(PrimaryRed, PrimaryPurple)
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
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                IconButton(onClick = { /* TODO: menu */ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = White)
                }
            }
            
            // Main Content Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(White)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Role Selection Section
                    item {
                        RoleSelectionSection(
                            selectedRole = selectedRole.value,
                            onRoleSelected = { selectedRole.value = it }
                        )
                    }
                    
                    // Basic Information Section
                    item {
                        BasicInformationSection(
                            name = name,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            passwordVisible = passwordVisible,
                            confirmPasswordVisible = confirmPasswordVisible
                        )
                    }
                    
                    // Skilled Person Additional Fields
                    item {
                        AnimatedVisibility(
                            visible = selectedRole.value == "Skilled Person",
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300)
                            ),
                            exit = slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(300)
                            )
                        ) {
                            SkilledPersonSection(
                                cnic = cnic,
                                phoneNumber = phoneNumber,
                                selectedTrade = selectedTrade,
                                yearsOfExperience = yearsOfExperience,
                                address = address,
                                trades = trades,
                                showTradeDropdown = showTradeDropdown
                            )
                        }
                    }
                    
                    // Sign Up Button
                    item {
                        Button(
                            onClick = {
                                if (isSkilledFormValid) {
                                    viewModel.signUp(
                                        name = name.value,
                                        email = email.value,
                                        password = password.value,
                                        role = selectedRole.value,
                                        cnic = if (selectedRole.value == "Skilled Person") cnic.value else "",
                                        phoneNumber = if (selectedRole.value == "Skilled Person") phoneNumber.value else "",
                                        selectedTrade = if (selectedRole.value == "Skilled Person") selectedTrade.value else "",
                                        yearsOfExperience = if (selectedRole.value == "Skilled Person") yearsOfExperience.value else "",
                                        address = if (selectedRole.value == "Skilled Person") address.value else "",
                                        onSuccess = onSignUp,
                                        onError = {}
                                    )
                                } else {
                                    showValidationError.value = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAF1117)),
                            enabled = isSkilledFormValid && authState !is AuthResult.Loading
                        ) {
                            if (authState is AuthResult.Loading) {
                                CircularProgressIndicator(
                                    color = White, 
                                    modifier = Modifier.size(24.dp), 
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "SIGN UP", 
                                    color = White, 
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Validation Error
                    item {
                        if (showValidationError.value && !isSkilledFormValid) {
                            val errorMsg = when {
                                name.value.isBlank() -> "Name is required."
                                email.value.isBlank() -> "Email is required."
                                password.value.isBlank() -> "Password is required."
                                confirmPassword.value.isBlank() -> "Confirm password is required."
                                password.value != confirmPassword.value -> "Passwords do not match."
                                selectedRole.value == "Skilled Person" && cnic.value.isBlank() -> "CNIC is required."
                                selectedRole.value == "Skilled Person" && phoneNumber.value.isBlank() -> "Phone number is required."
                                selectedRole.value == "Skilled Person" && selectedTrade.value.isBlank() -> "Trade is required."
                                selectedRole.value == "Skilled Person" && yearsOfExperience.value.isBlank() -> "Years of experience is required."
                                selectedRole.value == "Skilled Person" && address.value.isBlank() -> "Address is required."
                                else -> "Please fill all fields."
                            }
                            Text(
                                errorMsg, 
                                color = Color.Red, 
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        if (authState is AuthResult.Error) {
                            Text(
                                (authState as AuthResult.Error).message, 
                                color = Color.Red, 
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // Sign In Link
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Already have an account? ", 
                                color = Color(0xFF718096),
                                fontSize = 16.sp
                            )
                            TextButton(
                                onClick = onSignIn,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    "Sign In", 
                                    color = Color(0xFFAF1117),
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
}

@Composable
fun RoleSelectionSection(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Select your role:",
                color = Color(0xFF2D3748),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Customer Card
                RoleCard(
                    title = "Customer",
                    icon = Icons.Filled.Person,
                    description = "Looking for\nservices",
                    isSelected = selectedRole == "Customer",
                    onClick = { onRoleSelected("Customer") },
                    modifier = Modifier.weight(1f)
                )
                
                // Skilled Person Card
                RoleCard(
                    title = "Skilled Person",
                    icon = Icons.Filled.Build,
                    description = "Providing\nservices",
                    isSelected = selectedRole == "Skilled Person",
                    onClick = { onRoleSelected("Skilled Person") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFAF1117) else White
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) White else Color(0xFFAF1117),
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                title,
                color = if (isSelected) White else Color(0xFF2D3748),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                description,
                color = if (isSelected) White.copy(alpha = 0.9f) else Color(0xBEB22128),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun BasicInformationSection(
    name: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    confirmPasswordVisible: MutableState<Boolean>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFEFE)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Basic Information",
                color = Color(0xFF2D3748),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFFAF1117))
                },
                trailingIcon = {
                    if (name.value.isNotEmpty())
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF48BB78))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
            
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Phone or Gmail") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFFAF1117))
                },
                trailingIcon = {
                    if (email.value.isNotEmpty())
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF48BB78))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
            
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFAF1117))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(
                            if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = Color(0xFF718096)
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
            
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFAF1117))
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }) {
                        Icon(
                            if (confirmPasswordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = Color(0xFF718096)
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
        }
    }
}

@Composable
fun SkilledPersonSection(
    cnic: MutableState<String>,
    phoneNumber: MutableState<String>,
    selectedTrade: MutableState<String>,
    yearsOfExperience: MutableState<String>,
    address: MutableState<String>,
    trades: List<String>,
    showTradeDropdown: MutableState<Boolean>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFEFE)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Build,
                    contentDescription = null,
                    tint = Color(0xFFAF1117),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Professional Information",
                    color = Color(0xFF2D3748),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            OutlinedTextField(
                value = cnic.value,
                onValueChange = { 
                    // Format CNIC as 00000-0000000-0
                    val digitsOnly = it.filter { char -> char.isDigit() }
                    val formatted = when {
                        digitsOnly.length <= 5 -> digitsOnly
                        digitsOnly.length <= 12 -> "${digitsOnly.substring(0, 5)}-${digitsOnly.substring(5)}"
                        digitsOnly.length <= 13 -> "${digitsOnly.substring(0, 5)}-${digitsOnly.substring(5, 12)}-${digitsOnly.substring(12)}"
                        else -> "${digitsOnly.substring(0, 5)}-${digitsOnly.substring(5, 12)}-${digitsOnly.substring(12, 13)}"
                    }
                    if (digitsOnly.length <= 13) cnic.value = formatted
                },
                label = { Text("CNIC") },
                leadingIcon = {
                    Icon(Icons.Filled.Badge, contentDescription = null, tint = Color(0xFFAF1117))
                },
                placeholder = { Text("00000-0000000-0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
            
            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { 
                    // Format phone as +92-300-0000000
                    val digitsOnly = it.filter { char -> char.isDigit() }
                    val formatted = when {
                        digitsOnly.length <= 2 -> if (digitsOnly.startsWith("92")) "+$digitsOnly" else digitsOnly
                        digitsOnly.length <= 5 -> "+${digitsOnly.substring(0, 2)}-${digitsOnly.substring(2)}"
                        digitsOnly.length <= 12 -> "+${digitsOnly.substring(0, 2)}-${digitsOnly.substring(2, 5)}-${digitsOnly.substring(5)}"
                        else -> "+${digitsOnly.substring(0, 2)}-${digitsOnly.substring(2, 5)}-${digitsOnly.substring(5, 12)}"
                    }
                    if (digitsOnly.length <= 12) phoneNumber.value = formatted
                },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(Icons.Filled.Phone, contentDescription = null, tint = Color(0xFFAF1117))
                },
                placeholder = { Text("+92-300-0000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
            
            // Trade Dropdown
            Box {
                OutlinedTextField(
                    value = selectedTrade.value,
                    onValueChange = { },
                    label = { Text("Trade or Skill") },
                    leadingIcon = {
                        Icon(Icons.Filled.Work, contentDescription = null, tint = Color(0xFFAF1117))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showTradeDropdown.value = !showTradeDropdown.value }) {
                            Icon(
                                if (showTradeDropdown.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFFAF1117)
                            )
                        }
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTradeDropdown.value = !showTradeDropdown.value },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFAF1117),
                        focusedLabelColor = Color(0xFFAF1117)
                    )
                )
                
                DropdownMenu(
                    expanded = showTradeDropdown.value,
                    onDismissRequest = { showTradeDropdown.value = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    trades.forEach { trade ->
                        DropdownMenuItem(
                            text = { Text(trade) },
                            onClick = {
                                selectedTrade.value = trade
                                showTradeDropdown.value = false
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = yearsOfExperience.value,
                onValueChange = { 
                    // Only allow numbers
                    val digitsOnly = it.filter { char -> char.isDigit() }
                    if (digitsOnly.length <= 2) yearsOfExperience.value = digitsOnly
                },
                label = { Text("Years of Experience") },
                leadingIcon = {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFAF1117))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
            
            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text("Address") },
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFFAF1117))
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAF1117),
                    focusedLabelColor = Color(0xFFAF1117)
                )
            )
        }
    }
} 