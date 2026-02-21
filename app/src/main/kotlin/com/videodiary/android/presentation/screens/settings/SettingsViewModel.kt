package com.videodiary.android.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videodiary.android.data.local.datastore.TokenDataStore
import com.videodiary.android.domain.model.User
import com.videodiary.android.domain.model.WatermarkPosition
import com.videodiary.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LogoutState {
    data object Idle : LogoutState
    data object Loading : LogoutState
    data object Success : LogoutState
    data class Error(val message: String) : LogoutState
}

sealed interface ProfileState {
    data object Loading : ProfileState
    data class Loaded(val user: User) : ProfileState
    data object Error : ProfileState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    val isDarkModeEnabled = tokenDataStore.isDarkModeEnabled
    val notificationsEnabled = tokenDataStore.notificationsEnabled
    val watermarkPosition = tokenDataStore.watermarkPosition

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = try {
                ProfileState.Loaded(authRepository.getCurrentUser())
            } catch (e: Exception) {
                ProfileState.Error
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            _logoutState.value = try {
                authRepository.logout()
                LogoutState.Success
            } catch (e: Exception) {
                LogoutState.Error(e.message ?: "Logout failed. Please try again.")
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { tokenDataStore.updateDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { tokenDataStore.updateNotificationsEnabled(enabled) }
    }

    fun setWatermarkPosition(position: WatermarkPosition) {
        viewModelScope.launch { tokenDataStore.updateWatermarkPosition(position.name) }
    }
}
