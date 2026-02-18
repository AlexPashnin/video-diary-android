package com.videodiary.android.data.remote.api

import com.videodiary.android.data.remote.dto.auth.AuthResponse
import com.videodiary.android.data.remote.dto.auth.LoginRequest
import com.videodiary.android.data.remote.dto.auth.QuotaResponse
import com.videodiary.android.data.remote.dto.auth.RefreshRequest
import com.videodiary.android.data.remote.dto.auth.RegisterRequest
import com.videodiary.android.data.remote.dto.auth.UserResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout()

    @GET("auth/me")
    suspend fun me(): UserResponseDto

    @GET("users/quota")
    suspend fun getQuota(): QuotaResponse
}
