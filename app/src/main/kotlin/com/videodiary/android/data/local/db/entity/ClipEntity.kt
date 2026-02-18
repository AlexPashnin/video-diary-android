package com.videodiary.android.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clips")
data class ClipEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val videoId: String,
    val date: String, // ISO date string
    val status: String,
    val startTimeSeconds: Double,
    val objectKey: String?,
    val fileSize: Long?,
    val createdAt: Long, // epoch millis
    val updatedAt: Long, // epoch millis
)
