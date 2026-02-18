package com.videodiary.android.data.local.db.mapper

import com.videodiary.android.data.local.db.entity.CalendarDayEntity
import com.videodiary.android.data.local.db.entity.ClipEntity
import com.videodiary.android.data.local.db.entity.VideoEntity
import com.videodiary.android.domain.model.CalendarDay
import com.videodiary.android.domain.model.CalendarMonth
import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.model.ClipStatus
import com.videodiary.android.domain.model.Video
import com.videodiary.android.domain.model.VideoStatus
import java.time.Instant
import java.time.LocalDate

fun VideoEntity.toDomain(): Video = Video(
    id = id,
    userId = userId,
    date = LocalDate.parse(date),
    status = VideoStatus.valueOf(status),
    fileSize = fileSize,
    durationSeconds = durationSeconds,
    spriteSheetUrl = spriteSheetUrl,
    waveformUrl = waveformUrl,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

fun Video.toEntity(): VideoEntity = VideoEntity(
    id = id,
    userId = userId,
    date = date.toString(),
    status = status.name,
    fileSize = fileSize,
    durationSeconds = durationSeconds,
    spriteSheetUrl = spriteSheetUrl,
    waveformUrl = waveformUrl,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
)

fun ClipEntity.toDomain(): Clip = Clip(
    id = id,
    userId = userId,
    videoId = videoId,
    date = LocalDate.parse(date),
    status = ClipStatus.valueOf(status),
    startTimeSeconds = startTimeSeconds,
    objectKey = objectKey,
    fileSize = fileSize,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

fun Clip.toEntity(): ClipEntity = ClipEntity(
    id = id,
    userId = userId,
    videoId = videoId,
    date = date.toString(),
    status = status.name,
    startTimeSeconds = startTimeSeconds,
    objectKey = objectKey,
    fileSize = fileSize,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
)

fun CalendarDayEntity.toDomain(): CalendarDay = CalendarDay(
    date = LocalDate.parse(date),
    hasClip = hasClip,
    clipId = clipId,
    clipStatus = clipStatus?.let { ClipStatus.valueOf(it) },
)

fun CalendarDay.toEntity(year: Int, month: Int): CalendarDayEntity = CalendarDayEntity(
    date = date.toString(),
    hasClip = hasClip,
    clipId = clipId,
    clipStatus = clipStatus?.name,
    year = year,
    month = month,
)

fun List<CalendarDayEntity>.toCalendarMonth(year: Int, month: Int): CalendarMonth = CalendarMonth(
    year = year,
    month = month,
    days = map { it.toDomain() },
)
