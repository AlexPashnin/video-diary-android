package com.videodiary.android.data.remote.mapper

import com.videodiary.android.data.remote.dto.compilation.CompilationResponseDto
import com.videodiary.android.data.remote.dto.compilation.CompilationStatusResponseDto
import com.videodiary.android.domain.model.Compilation
import com.videodiary.android.domain.model.CompilationProgress
import com.videodiary.android.domain.model.CompilationStatus
import com.videodiary.android.domain.model.QualityOption
import com.videodiary.android.domain.model.WatermarkPosition
import java.time.Instant
import java.time.LocalDate

fun CompilationResponseDto.toDomain(): Compilation = Compilation(
    id = id,
    userId = userId,
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    status = CompilationStatus.valueOf(status),
    quality = QualityOption.valueOf(quality),
    watermarkPosition = WatermarkPosition.valueOf(watermarkPosition),
    clipCount = clipCount,
    clipIds = clipIds,
    objectKey = objectKey,
    fileSizeBytes = fileSizeBytes,
    durationSeconds = durationSeconds,
    expiresAt = expiresAt?.let { Instant.parse(it) },
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

fun CompilationStatusResponseDto.toDomain(): CompilationProgress = CompilationProgress(
    id = id,
    status = CompilationStatus.valueOf(status),
    clipCount = clipCount,
    currentClip = currentClip,
    percentComplete = percentComplete,
)
