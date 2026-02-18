package com.videodiary.android.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.videodiary.android.data.local.db.entity.ClipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(clip: ClipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(clips: List<ClipEntity>)

    @Query("SELECT * FROM clips WHERE id = :id")
    fun observeById(id: String): Flow<ClipEntity?>

    @Query("SELECT * FROM clips WHERE id = :id")
    suspend fun getById(id: String): ClipEntity?

    @Query("SELECT * FROM clips ORDER BY date DESC")
    suspend fun getAll(): List<ClipEntity>

    @Query("DELETE FROM clips WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM clips")
    suspend fun deleteAll()
}
