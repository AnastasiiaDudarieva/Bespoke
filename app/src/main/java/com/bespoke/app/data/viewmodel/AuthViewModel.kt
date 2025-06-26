package com.bespoke.app.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.FirebaseRepository
import com.bespoke.app.ui.models.auth.AuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (_authState.value == AuthState.Loading)
            return
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Invalid Username or Password")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Success(it.user?.uid.orEmpty())
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Unknown error")
            }
    }

    fun checkIfLoggedIn() {
        val user = auth.currentUser
        if (user != null) {
            _authState.value = AuthState.Success(user.uid)
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetPassword(
        email: String,
    ) {
        if (_authState.value == AuthState.Loading)
            return
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("An email address must be provider")
            return
        }
        _authState.value = AuthState.Loading
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Message("Check your email")
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.localizedMessage ?: "Unknown error")
                }
            }
    }

    fun requestInvite(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = firebaseRepository.requestInvite(email)
            result
                .onSuccess { (success, message) ->
                    if (success) {
                        _authState.value = AuthState.Idle
                    } else {
                        _authState.value = AuthState.Error(message ?: "Unknown error")
                    }
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Unknown error")
                }
        }
    }
}
