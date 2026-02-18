package com.videodiary.android.data.remote.mapper

import com.videodiary.android.data.remote.dto.clip.CalendarDayResponseDto
import com.videodiary.android.data.remote.dto.clip.CalendarMonthResponseDto
import com.videodiary.android.data.remote.dto.clip.ClipResponseDto
import com.videodiary.android.domain.model.CalendarDay
import com.videodiary.android.domain.model.CalendarMonth
import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.model.ClipStatus
import java.time.Instant
import java.time.LocalDate

fun ClipResponseDto.toDomain(): Clip = Clip(
    id = id,
    userId = userId,
    videoId = videoId,
    date = LocalDate.parse(date),
    status = ClipStatus.valueOf(status),
    startTimeSeconds = startTimeSeconds,
    objectKey = objectKey,
    fileSize = fileSize,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

fun CalendarDayResponseDto.toDomain(): CalendarDay = CalendarDay(
    date = LocalDate.parse(date),
    hasClip = hasClip,
    clipId = clipId,
    clipStatus = status?.let { ClipStatus.valueOf(it) },
)

fun CalendarMonthResponseDto.toDomain(): CalendarMonth = CalendarMonth(
    year = year,
    month = month,
    days = days.map { it.toDomain() },
)
