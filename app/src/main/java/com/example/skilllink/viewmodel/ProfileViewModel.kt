package com.example.skilllink.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ProfileResult {
    object Success : ProfileResult()
    data class Error(val message: String) : ProfileResult()
    object Loading : ProfileResult()
    object Idle : ProfileResult()
}

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val role: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _profileState = MutableStateFlow<ProfileResult>(ProfileResult.Idle)
    val profileState: StateFlow<ProfileResult> = _profileState

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        _profileState.value = ProfileResult.Loading
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val profile = UserProfile(
                            uid = document.getString("uid") ?: "",
                            fullName = document.getString("fullName") ?: document.getString("name") ?: "",
                            email = document.getString("email") ?: "",
                            phone = document.getString("phone") ?: "",
                            profileImageUrl = document.getString("profileImageUrl") ?: "",
                            role = document.getString("role") ?: ""
                        )
                        _userProfile.value = profile
                        _profileState.value = ProfileResult.Success
                    } else {
                        _profileState.value = ProfileResult.Error("User profile not found")
                    }
                }
                .addOnFailureListener { e ->
                    _profileState.value = ProfileResult.Error(e.message ?: "Failed to load profile")
                }
        } else {
            _profileState.value = ProfileResult.Error("User not authenticated")
        }
    }

    fun updateProfile(fullName: String, email: String, phone: String) {
        _profileState.value = ProfileResult.Loading
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val updates = hashMapOf<String, Any>(
                "fullName" to fullName,
                "email" to email,
                "phone" to phone
            )

            firestore.collection("users").document(currentUser.uid)
                .update(updates)
                .addOnSuccessListener {
                    _userProfile.value = _userProfile.value.copy(
                        fullName = fullName,
                        email = email,
                        phone = phone
                    )
                    _profileState.value = ProfileResult.Success
                }
                .addOnFailureListener { e ->
                    _profileState.value = ProfileResult.Error(e.message ?: "Failed to update profile")
                }
        } else {
            _profileState.value = ProfileResult.Error("User not authenticated")
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        _profileState.value = ProfileResult.Loading
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val imageFileName = "profile_images/${currentUser.uid}_${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child(imageFileName)

            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Update the profile image URL in Firestore
                        firestore.collection("users").document(currentUser.uid)
                            .update("profileImageUrl", downloadUri.toString())
                            .addOnSuccessListener {
                                _userProfile.value = _userProfile.value.copy(
                                    profileImageUrl = downloadUri.toString()
                                )
                                _selectedImageUri.value = null // Clear selected image
                                _profileState.value = ProfileResult.Success
                            }
                            .addOnFailureListener { e ->
                                _profileState.value = ProfileResult.Error(e.message ?: "Failed to update profile image URL")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    _profileState.value = ProfileResult.Error(e.message ?: "Failed to upload image")
                }
        } else {
            _profileState.value = ProfileResult.Error("User not authenticated")
        }
    }

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
        // Automatically upload the image when selected
        uri?.let { uploadProfileImage(it) }
    }

    fun resetState() {
        _profileState.value = ProfileResult.Idle
    }
} 