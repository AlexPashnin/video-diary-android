package com.videodiary.android.data.remote.api

import com.videodiary.android.data.remote.dto.video.InitiateUploadRequest
import com.videodiary.android.data.remote.dto.video.InitiateUploadResponse
import com.videodiary.android.data.remote.dto.video.VideoPageResponse
import com.videodiary.android.data.remote.dto.video.VideoResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoApi {
    @POST("videos/upload/initiate")
    suspend fun initiateUpload(
        @Body request: InitiateUploadRequest,
    ): InitiateUploadResponse

    @POST("videos/{videoId}/upload/complete")
    suspend fun completeUpload(
        @Path("videoId") videoId: String,
    )

    @GET("videos")
    suspend fun listVideos(
        @Query("date") date: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): VideoPageResponse

    @GET("videos/{videoId}")
    suspend fun getVideo(
        @Path("videoId") videoId: String,
    ): VideoResponseDto

    @DELETE("videos/{videoId}")
    suspend fun deleteVideo(
        @Path("videoId") videoId: String,
    )
}
