package com.example.myfit_exercisecompanion.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myfit_exercisecompanion.models.RunSession

@Dao
interface RunSessionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunSession(runSession: RunSession)

    @Delete
    fun delete(runSession: RunSession)

    @Query("SELECT * FROM runsession ORDER BY timestamp DESC")
    fun getAllRunSessionsSortedByDate(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession ORDER BY timeInMilis DESC")
    fun getAllRunSessionsSortedByTimeInMilis(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession ORDER BY caloriesBurnt DESC")
    fun getAllRunSessionsSortedByCaloriesBurnt(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession ORDER BY avgSpeedInKMH DESC")
    fun getAllRunSessionsSortedByAvgSpeed(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession ORDER BY distanceInMeters DESC")
    fun getAllRunSessionsSortedByDistance(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession")
    fun getAllRunSessions(): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession ORDER BY timestamp ASC LIMIT 1")
    fun getMostRecentRunSession(): LiveData<RunSession>

    @Query("SELECT SUM(timeInMilis) FROM runsession")
    fun getTotalTimeInMilis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurnt) FROM runsession")
    fun getTotalCaloriesBurnt(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM runsession")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT SUM(avgSpeedInKMH) FROM runsession")
    fun getTotalAvgSpeed(): LiveData<Float>


}