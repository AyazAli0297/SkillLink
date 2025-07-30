package com.example.skilllink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilllink.ui.screens.ServiceProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProviderViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _providers = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val providers: StateFlow<List<ServiceProvider>> = _providers
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchProvidersByTrade(trade: String) {
        _loading.value = true
        firestore.collection("users")
            .whereEqualTo("role", "provider")
            .whereEqualTo("trade", trade)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    ServiceProvider(
                        uid = doc.getString("uid") ?: "",
                        fullName = doc.getString("fullName") ?: "",
                        phone = doc.getString("phone") ?: "",
                        experience = doc.getString("experience") ?: "",
                        trade = doc.getString("trade") ?: "",
                        address = doc.getString("address") ?: ""
                    )
                }
                _providers.value = list
                _loading.value = false
            }
            .addOnFailureListener {
                _providers.value = emptyList()
                _loading.value = false
            }
    }
} 