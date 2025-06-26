package com.bespoke.app.viewmodel

import androidx.lifecycle.ViewModel
import com.bespoke.app.ui.models.AuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
}
