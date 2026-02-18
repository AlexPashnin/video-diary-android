package com.videodiary.android.data.remote.dto.compilation

import kotlinx.serialization.Serializable

@Serializable
data class CreateCompilationRequest(
    val startDate: String,
    val endDate: String,
    val quality: String,
    val watermarkPosition: String,
    val clipIds: List<String>,
)

@Serializable
data class CompilationResponseDto(
    val id: String,
    val userId: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val quality: String,
    val watermarkPosition: String,
    val clipCount: Int,
    val clipIds: List<String>,
    val objectKey: String? = null,
    val fileSizeBytes: Long? = null,
    val durationSeconds: Double? = null,
    val expiresAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CompilationStatusResponseDto(
    val id: String,
    val status: String,
    val clipCount: Int,
    val currentClip: Int? = null,
    val percentComplete: Int? = null,
)

@Serializable
data class CompilationPageResponse(
    val content: List<CompilationResponseDto>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
