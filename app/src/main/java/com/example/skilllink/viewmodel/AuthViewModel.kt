package com.example.skilllink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Idle : AuthResult()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun signUp(
        name: String,
        email: String,
        password: String,
        role: String,
        // Additional fields for skilled persons
        cnic: String = "",
        phoneNumber: String = "",
        selectedTrade: String = "",
        yearsOfExperience: String = "",
        address: String = "",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        val user = hashMapOf(
                            "uid" to uid,
                            "email" to email,
                            "role" to if (role == "Customer") "customer" else "provider",
                            "name" to name,
                            "fullName" to name,
                            "profileComplete" to true
                        )
                        
                        // Add skilled person specific fields if role is Skilled Person
                        if (role == "Skilled Person") {
                            user["cnic"] = cnic
                            user["phone"] = phoneNumber
                            user["phoneNumber"] = phoneNumber
                            user["trade"] = selectedTrade
                            user["experience"] = yearsOfExperience
                            user["yearsOfExperience"] = yearsOfExperience
                            user["address"] = address
                        }
                        
                        firestore.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                _authState.value = AuthResult.Success
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthResult.Error(e.message ?: "Firestore error")
                                onError(e.message ?: "Firestore error")
                            }
                    } else {
                        _authState.value = AuthResult.Error(task.exception?.message ?: "Auth error")
                        onError(task.exception?.message ?: "Auth error")
                    }
                }
        }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (String) -> Unit = {}, // role: "customer" or "provider"
        onError: (String) -> Unit = {}
    ) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role") ?: ""
                                _authState.value = AuthResult.Success
                                onSuccess(role)
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthResult.Error(e.message ?: "Failed to fetch user role")
                                onError(e.message ?: "Failed to fetch user role")
                            }
                    } else {
                        _authState.value = AuthResult.Error(task.exception?.message ?: "Sign in error")
                        onError(task.exception?.message ?: "Sign in error")
                    }
                }
        }
    }

    fun resetState() {
        _authState.value = AuthResult.Idle
    }
} 