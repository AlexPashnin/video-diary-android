package com.videodiary.android.data.remote.api

import com.videodiary.android.data.remote.dto.notification.RegisterDeviceRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApi {
    @POST("notifications/register-device")
    suspend fun registerDevice(
        @Body request: RegisterDeviceRequest,
    )
}
