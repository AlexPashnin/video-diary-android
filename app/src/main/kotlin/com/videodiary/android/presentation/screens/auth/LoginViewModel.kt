package com.videodiary.android.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.repository.AuthRepository
import com.videodiary.android.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginState {
    data object Idle : LoginState

    data object Loading : LoginState

    data object Success : LoginState

    data class Error(val message: String) : LoginState
}

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
        val state: StateFlow<LoginState> = _state.asStateFlow()

        fun login(
            email: String,
            password: String,
        ) {
            if (!email.isValidEmail()) {
                _state.value = LoginState.Error("Please enter a valid email address.")
                return
            }
            if (password.isBlank()) {
                _state.value = LoginState.Error("Password cannot be empty.")
                return
            }
            viewModelScope.launch {
                _state.value = LoginState.Loading
                _state.value =
                    try {
                        authRepository.login(email.trim(), password)
                        LoginState.Success
                    } catch (e: Exception) {
                        LoginState.Error(e.message ?: "Login failed. Please try again.")
                    }
            }
        }

        fun resetState() {
            _state.value = LoginState.Idle
        }
    }
