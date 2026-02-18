package com.videodiary.android.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.videodiary.android.data.local.db.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(videos: List<VideoEntity>)

    @Query("SELECT * FROM videos WHERE id = :id")
    fun observeById(id: String): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE id = :id")
    suspend fun getById(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): VideoEntity?

    @Query("SELECT * FROM videos ORDER BY date DESC")
    suspend fun getAll(): List<VideoEntity>

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM videos")
    suspend fun deleteAll()
}
