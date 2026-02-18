package com.videodiary.android.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val timezone: String? = null,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserResponseDto,
)

@Serializable
data class UserResponseDto(
    val id: String,
    val email: String,
    val displayName: String,
    val tier: String,
    val emailVerified: Boolean,
    val profilePictureUrl: String? = null,
    val timezone: String,
    val defaultWatermarkPosition: String,
    val notificationsEnabled: Boolean,
    val createdAt: String,
)

@Serializable
data class QuotaResponse(
    val tier: String,
    val limits: TierLimitsDto,
)

@Serializable
data class TierLimitsDto(
    val maxVideosPerDay: Int,
    val maxVideoSizeMb: Int,
    val maxCompilationDays: Int,
    val compilationRetentionDays: Int,
)
