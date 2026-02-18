package com.videodiary.android.data.remote.mapper

import com.videodiary.android.data.remote.dto.video.VideoResponseDto
import com.videodiary.android.domain.model.Video
import com.videodiary.android.domain.model.VideoStatus
import java.time.Instant
import java.time.LocalDate

fun VideoResponseDto.toDomain(): Video = Video(
    id = id,
    userId = userId,
    date = LocalDate.parse(date),
    status = VideoStatus.valueOf(status),
    fileSize = fileSize,
    durationSeconds = durationSeconds,
    spriteSheetUrl = spriteSheetUrl,
    waveformUrl = waveformUrl,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)
