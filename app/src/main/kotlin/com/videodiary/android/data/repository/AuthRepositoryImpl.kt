package com.videodiary.android.data.repository

import com.videodiary.android.data.local.datastore.TokenDataStore
import com.videodiary.android.data.remote.api.AuthApi
import com.videodiary.android.data.remote.dto.auth.LoginRequest
import com.videodiary.android.data.remote.dto.auth.RefreshRequest
import com.videodiary.android.data.remote.dto.auth.RegisterRequest
import com.videodiary.android.data.remote.mapper.toDomain
import com.videodiary.android.domain.model.User
import com.videodiary.android.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        val response = authApi.login(LoginRequest(email, password))
        tokenDataStore.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresIn = response.expiresIn,
            userId = response.user.id,
            userTier = response.user.tier,
        )
        return response.user.toDomain()
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String,
        timezone: String?,
    ): User {
        val response = authApi.register(RegisterRequest(email, password, displayName, timezone))
        tokenDataStore.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresIn = response.expiresIn,
            userId = response.user.id,
            userTier = response.user.tier,
        )
        return response.user.toDomain()
    }

    override suspend fun refreshToken(): Boolean {
        val refreshToken = tokenDataStore.getRefreshToken() ?: return false
        return try {
            val response = authApi.refresh(RefreshRequest(refreshToken))
            tokenDataStore.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn,
                userId = response.user.id,
                userTier = response.user.tier,
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout() {
        try {
            authApi.logout()
        } finally {
            tokenDataStore.clearTokens()
        }
    }

    override suspend fun getCurrentUser(): User = authApi.me().toDomain()

    override suspend fun isLoggedIn(): Boolean {
        val token = tokenDataStore.getAccessToken() ?: return false
        return token.isNotBlank() && !tokenDataStore.isTokenExpired()
    }
}
