package com.videodiary.android.data.remote.dto.video

import kotlinx.serialization.Serializable

@Serializable
data class InitiateUploadRequest(
    val date: String, // ISO date: yyyy-MM-dd
)

@Serializable
data class InitiateUploadResponse(
    val videoId: String,
    val uploadUrl: String,
)

@Serializable
data class VideoResponseDto(
    val id: String,
    val userId: String,
    val date: String,
    val status: String,
    val fileSize: Long? = null,
    val durationSeconds: Double? = null,
    val spriteSheetUrl: String? = null,
    val waveformUrl: String? = null,
    val videoUrl: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class VideoPageResponse(
    val content: List<VideoResponseDto>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
