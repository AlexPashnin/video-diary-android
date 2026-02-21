package com.videodiary.android.di

import android.content.Context
import androidx.room.Room
import com.videodiary.android.data.local.db.VideoDiaryDatabase
import com.videodiary.android.data.local.db.dao.CalendarDayDao
import com.videodiary.android.data.local.db.dao.ClipDao
import com.videodiary.android.data.local.db.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): VideoDiaryDatabase =
        Room.databaseBuilder(context, VideoDiaryDatabase::class.java, "videodiary.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideVideoDao(db: VideoDiaryDatabase): VideoDao = db.videoDao()

    @Provides
    fun provideClipDao(db: VideoDiaryDatabase): ClipDao = db.clipDao()

    @Provides
    fun provideCalendarDayDao(db: VideoDiaryDatabase): CalendarDayDao = db.calendarDayDao()
}
