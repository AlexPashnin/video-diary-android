package com.videodiary.android.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val userId: String,
    // ISO date string
    val date: String,
    val status: String,
    val fileSize: Long?,
    val durationSeconds: Double?,
    val spriteSheetUrl: String?,
    val waveformUrl: String?,
    val videoUrl: String?,
    // epoch millis
    val createdAt: Long,
    // epoch millis
    val updatedAt: Long,
)
