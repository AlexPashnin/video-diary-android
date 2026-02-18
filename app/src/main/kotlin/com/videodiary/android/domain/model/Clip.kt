package com.videodiary.android.domain.model

import java.time.Instant
import java.time.LocalDate

data class Clip(
    val id: String,
    val userId: String,
    val videoId: String,
    val date: LocalDate,
    val status: ClipStatus,
    val startTimeSeconds: Double,
    val objectKey: String?,
    val fileSize: Long?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class ClipStatus { EXTRACTING, READY, FAILED }
