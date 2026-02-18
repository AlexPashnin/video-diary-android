package com.videodiary.android.domain.model

import java.time.Instant

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val tier: UserTier,
    val emailVerified: Boolean,
    val profilePictureUrl: String?,
    val timezone: String,
    val defaultWatermarkPosition: WatermarkPosition,
    val notificationsEnabled: Boolean,
    val createdAt: Instant,
)

enum class UserTier { FREE, PREMIUM, ENTERPRISE }
