package com.videodiary.android.domain.repository

import com.videodiary.android.domain.model.Clip
import com.videodiary.android.domain.model.CalendarMonth
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ClipRepository {
    suspend fun selectClip(videoId: String, date: LocalDate, startTimeSeconds: Double): Clip
    suspend fun getClip(clipId: String): Clip
    fun observeClip(clipId: String): Flow<Clip>
    suspend fun listClips(page: Int = 0): List<Clip>
    suspend fun getCalendar(year: Int, month: Int): CalendarMonth
    fun observeCalendar(year: Int, month: Int): Flow<CalendarMonth>
    suspend fun deleteClip(clipId: String)
}
