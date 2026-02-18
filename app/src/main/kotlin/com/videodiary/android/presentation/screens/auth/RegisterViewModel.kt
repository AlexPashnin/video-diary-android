package com.videodiary.android.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

sealed interface RegisterState {
    data object Idle : RegisterState
    data object Loading : RegisterState
    data object Success : RegisterState
    data class Error(val message: String) : RegisterState
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun register(email: String, password: String, displayName: String) {
        if (email.isBlank() || password.length < 8 || displayName.isBlank()) return
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            _state.value = try {
                val timezone = TimeZone.getDefault().id
                authRepository.register(email, password, displayName, timezone)
                RegisterState.Success
            } catch (e: Exception) {
                RegisterState.Error(e.message ?: "Registration failed. Please try again.")
            }
        }
    }
}
