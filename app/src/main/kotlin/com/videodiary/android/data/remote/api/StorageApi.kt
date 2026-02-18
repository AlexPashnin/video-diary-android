package com.videodiary.android.data.remote.api

import com.videodiary.android.data.remote.dto.storage.ObjectInfoResponse
import com.videodiary.android.data.remote.dto.storage.PresignedDownloadRequest
import com.videodiary.android.data.remote.dto.storage.PresignedUploadRequest
import com.videodiary.android.data.remote.dto.storage.PresignedUrlResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StorageApi {
    @POST("storage/presigned-url/upload")
    suspend fun generateUploadUrl(@Body request: PresignedUploadRequest): PresignedUrlResponse

    @POST("storage/presigned-url/download")
    suspend fun generateDownloadUrl(@Body request: PresignedDownloadRequest): PresignedUrlResponse

    @GET("storage/objects/info")
    suspend fun getObjectInfo(
        @Query("bucket") bucket: String,
        @Query("objectKey") objectKey: String,
    ): ObjectInfoResponse

    @DELETE("storage/objects")
    suspend fun deleteObject(
        @Query("bucket") bucket: String,
        @Query("objectKey") objectKey: String,
    )
}
