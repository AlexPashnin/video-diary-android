package com.videodiary.android.data.remote.dto.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceRequest(
    @SerialName("fcmToken") val fcmToken: String,
    @SerialName("platform") val platform: String = "android",
)
