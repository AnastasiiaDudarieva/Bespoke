package com.bespoke.app.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.repository.FirebaseRepository
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.ui.models.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            memberRepository.member.collect { member ->
                checkIfLoggedIn()
            }
        }
    }

    private fun checkIfLoggedIn() {
        val uid = memberRepository.currentUserId()
        if (uid != null) {
            _authState.value = AuthState.Success(uid)
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun login(email: String, password: String) {
        if (_authState.value == AuthState.Loading) return
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Invalid Username or Password")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = memberRepository.login(email, password)
            result
                .onSuccess { uid ->
                    memberRepository.loadMemberById(uid)
                }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Unknown error") }
        }
    }


    fun resetPassword(email: String) {
        if (_authState.value == AuthState.Loading) return
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("An email address must be provided")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = memberRepository.resetPassword(email)
            result
                .onSuccess { _authState.value = AuthState.Message("Check your email") }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Unknown error") }
        }
    }

    fun requestInvite(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = firebaseRepository.requestInvite(email)
            if (result.status) {
                _authState.value = AuthState.Message(result.message ?: "Success")
            } else {
                _authState.value = AuthState.Error(result.errorMessage ?: "Unknown error")
            }
        }
    }
}

