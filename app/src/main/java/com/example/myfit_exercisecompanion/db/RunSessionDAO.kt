package com.example.myfit_exercisecompanion.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myfit_exercisecompanion.models.RunSession

@Dao
interface RunSessionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(runSession: RunSession): Long

    @Query("SELECT * FROM runsession")
    fun getAllRunSessions(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession ORDER BY id DESC LIMIT 1")
    fun getMostRecentRunSession(): LiveData<RunSession>

    @Delete
    fun delete(runSession: RunSession)
}