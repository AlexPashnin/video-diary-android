package com.videodiary.android.data.remote.dto.storage

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUploadRequest(
    val bucket: String,
    val objectKey: String,
)

@Serializable
data class PresignedDownloadRequest(
    val bucket: String,
    val objectKey: String,
)

@Serializable
data class PresignedUrlResponse(
    val url: String,
    val bucket: String,
    val objectKey: String,
)

@Serializable
data class ObjectInfoResponse(
    val bucket: String,
    val objectKey: String,
    val exists: Boolean,
    val size: Long? = null,
)
