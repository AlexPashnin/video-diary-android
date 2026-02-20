package com.videodiary.android.domain.model

import java.time.Instant
import java.time.LocalDate

data class Video(
    val id: String,
    val userId: String,
    val date: LocalDate,
    val status: VideoStatus,
    val fileSize: Long?,
    val durationSeconds: Double?,
    val spriteSheetUrl: String?,
    val waveformUrl: String?,
    val videoUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class VideoStatus { UPLOADING, PROCESSING, READY, FAILED, CLIP_EXTRACTED }
