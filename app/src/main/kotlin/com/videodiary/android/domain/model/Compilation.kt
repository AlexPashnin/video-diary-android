package com.videodiary.android.domain.model

import java.time.Instant
import java.time.LocalDate

data class Compilation(
    val id: String,
    val userId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: CompilationStatus,
    val quality: QualityOption,
    val watermarkPosition: WatermarkPosition,
    val clipCount: Int,
    val clipIds: List<String>,
    val objectKey: String?,
    val fileSizeBytes: Long?,
    val durationSeconds: Double?,
    val expiresAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class CompilationProgress(
    val id: String,
    val status: CompilationStatus,
    val clipCount: Int,
    val currentClip: Int?,
    val percentComplete: Int?,
)

enum class CompilationStatus { PENDING, PROCESSING, COMPLETED, FAILED }

enum class QualityOption { Q_480P, Q_720P, Q_1080P, Q_4K }

enum class WatermarkPosition {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER_TOP, CENTER_BOTTOM
}
