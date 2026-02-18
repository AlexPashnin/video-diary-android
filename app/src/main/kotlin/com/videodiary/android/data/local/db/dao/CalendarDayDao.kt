package com.videodiary.android.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.videodiary.android.data.local.db.entity.CalendarDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(days: List<CalendarDayEntity>)

    @Query("SELECT * FROM calendar_days WHERE year = :year AND month = :month ORDER BY date ASC")
    fun observeByMonth(year: Int, month: Int): Flow<List<CalendarDayEntity>>

    @Query("SELECT * FROM calendar_days WHERE year = :year AND month = :month ORDER BY date ASC")
    suspend fun getByMonth(year: Int, month: Int): List<CalendarDayEntity>

    @Query("DELETE FROM calendar_days WHERE year = :year AND month = :month")
    suspend fun deleteByMonth(year: Int, month: Int)

    @Query("DELETE FROM calendar_days")
    suspend fun deleteAll()
}
