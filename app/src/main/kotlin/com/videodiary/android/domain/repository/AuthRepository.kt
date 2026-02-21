package com.videodiary.android.domain.repository

import com.videodiary.android.domain.model.User

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): User

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        timezone: String?,
    ): User

    suspend fun refreshToken(): Boolean

    suspend fun logout()

    suspend fun getCurrentUser(): User

    suspend fun isLoggedIn(): Boolean
}
