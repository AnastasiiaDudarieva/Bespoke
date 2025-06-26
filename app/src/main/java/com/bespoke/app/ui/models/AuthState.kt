package com.bespoke.app.ui.models

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class Message(val message: String) : AuthState()
}