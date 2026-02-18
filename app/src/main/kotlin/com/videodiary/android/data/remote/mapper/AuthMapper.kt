package com.videodiary.android.data.remote.mapper

import com.videodiary.android.data.remote.dto.auth.UserResponseDto
import com.videodiary.android.domain.model.User
import com.videodiary.android.domain.model.UserTier
import com.videodiary.android.domain.model.WatermarkPosition
import java.time.Instant

fun UserResponseDto.toDomain(): User = User(
    id = id,
    email = email,
    displayName = displayName,
    tier = UserTier.valueOf(tier),
    emailVerified = emailVerified,
    profilePictureUrl = profilePictureUrl,
    timezone = timezone,
    defaultWatermarkPosition = WatermarkPosition.valueOf(defaultWatermarkPosition),
    notificationsEnabled = notificationsEnabled,
    createdAt = Instant.parse(createdAt),
)
