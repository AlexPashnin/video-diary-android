package com.videodiary.android.data.remote.api

import com.videodiary.android.data.remote.dto.compilation.CompilationPageResponse
import com.videodiary.android.data.remote.dto.compilation.CompilationResponseDto
import com.videodiary.android.data.remote.dto.compilation.CompilationStatusResponseDto
import com.videodiary.android.data.remote.dto.compilation.CreateCompilationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CompilationApi {
    @POST("compilations/create")
    suspend fun createCompilation(@Body request: CreateCompilationRequest): CompilationResponseDto

    @GET("compilations/{compilationId}")
    suspend fun getCompilation(@Path("compilationId") compilationId: String): CompilationResponseDto

    @GET("compilations/{compilationId}/status")
    suspend fun getCompilationStatus(@Path("compilationId") compilationId: String): CompilationStatusResponseDto

    @GET("compilations/history")
    suspend fun listCompilations(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): CompilationPageResponse

    @DELETE("compilations/{compilationId}")
    suspend fun deleteCompilation(@Path("compilationId") compilationId: String)
}
