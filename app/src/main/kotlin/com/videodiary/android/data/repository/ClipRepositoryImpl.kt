package com.videodiary.android.data.repository

import com.videodiary.android.data.local.db.dao.CalendarDayDao
import com.videodiary.android.data.local.db.dao.ClipDao
import com.videodiary.android.data.local.db.mapper.toCalendarMonth
import com.videodiary.android.data.local.db.mapper.toDomain
import com.videodiary.android.data.local.db.mapper.toEntity
import com.videodiary.android.data.remote.api.ClipApi
import com.videodiary.android.data.remote.dto.clip.SelectClipRequest
import com.videodiary.android.data.remote.mapper.toDomain
import com.videodiary.android.domain.model.CalendarMonth
import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.repository.ClipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipRepositoryImpl
    @Inject
    constructor(
        private val clipApi: ClipApi,
        private val clipDao: ClipDao,
        private val calendarDayDao: CalendarDayDao,
    ) : ClipRepository {
        override suspend fun selectClip(
            videoId: String,
            date: LocalDate,
            startTimeSeconds: Double,
        ): Clip {
            val clip =
                clipApi.selectClip(
                    SelectClipRequest(videoId, date.toString(), startTimeSeconds),
                ).toDomain()
            clipDao.upsert(clip.toEntity())
            return clip
        }

        override suspend fun getClip(clipId: String): Clip {
            val clip = clipApi.getClip(clipId).toDomain()
            clipDao.upsert(clip.toEntity())
            return clip
        }

        override fun observeClip(clipId: String): Flow<Clip> =
            clipDao.observeById(clipId).filterNotNull().map { it.toDomain() }

        override suspend fun listClips(page: Int): List<Clip> {
            return try {
                val clips = clipApi.listClips(page = page).content.map { it.toDomain() }
                if (page == 0) clipDao.upsertAll(clips.map { it.toEntity() })
                clips
            } catch (e: IOException) {
                if (page == 0) clipDao.getAll().map { it.toDomain() } else throw e
            }
        }

        override suspend fun getCalendar(
            year: Int,
            month: Int,
        ): CalendarMonth {
            return try {
                val remote = clipApi.getCalendar(year, month).toDomain()
                calendarDayDao.deleteByMonth(year, month)
                calendarDayDao.upsertAll(remote.days.map { it.toEntity(year, month) })
                remote
            } catch (e: IOException) {
                val cached = calendarDayDao.getByMonth(year, month)
                if (cached.isNotEmpty()) cached.toCalendarMonth(year, month) else throw e
            }
        }

        override fun observeCalendar(
            year: Int,
            month: Int,
        ): Flow<CalendarMonth> = calendarDayDao.observeByMonth(year, month).map { it.toCalendarMonth(year, month) }

        override suspend fun deleteClip(clipId: String) {
            clipApi.deleteClip(clipId)
            clipDao.deleteById(clipId)
        }
    }
