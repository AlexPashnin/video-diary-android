package com.videodiary.android.data.remote.dto.clip

import kotlinx.serialization.Serializable

@Serializable
data class SelectClipRequest(
    val videoId: String,
    // ISO date: yyyy-MM-dd
    val date: String,
    val startTimeSeconds: Double,
)

@Serializable
data class ClipResponseDto(
    val id: String,
    val userId: String,
    val videoId: String,
    val date: String,
    val status: String,
    val startTimeSeconds: Double,
    val objectKey: String? = null,
    val fileSize: Long? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class ClipPageResponse(
    val content: List<ClipResponseDto>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

@Serializable
data class CalendarDayResponseDto(
    val date: String,
    val hasClip: Boolean,
    val clipId: String? = null,
    val status: String? = null,
)

@Serializable
data class CalendarMonthResponseDto(
    val year: Int,
    val month: Int,
    val days: List<CalendarDayResponseDto>,
)
