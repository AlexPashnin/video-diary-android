package com.videodiary.android.data.local.db.entity

import androidx.room.Entity

@Entity(tableName = "calendar_days", primaryKeys = ["date"])
data class CalendarDayEntity(
    // ISO date string, primary key
    val date: String,
    val hasClip: Boolean,
    val clipId: String?,
    val clipStatus: String?,
    val year: Int,
    val month: Int,
)
