package com.videodiary.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.videodiary.android.data.local.db.dao.CalendarDayDao
import com.videodiary.android.data.local.db.dao.ClipDao
import com.videodiary.android.data.local.db.dao.VideoDao
import com.videodiary.android.data.local.db.entity.CalendarDayEntity
import com.videodiary.android.data.local.db.entity.ClipEntity
import com.videodiary.android.data.local.db.entity.VideoEntity

@Database(
    entities = [VideoEntity::class, ClipEntity::class, CalendarDayEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class VideoDiaryDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao

    abstract fun clipDao(): ClipDao

    abstract fun calendarDayDao(): CalendarDayDao
}
