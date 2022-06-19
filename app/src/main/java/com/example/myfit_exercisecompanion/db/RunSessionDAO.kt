package com.example.myfit_exercisecompanion.db


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myfit_exercisecompanion.models.RunSession

@Dao
interface RunSessionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunSession(runSession: RunSession)

    @Delete
    suspend fun deleteRunSession(runSession: RunSession)

    @Update
    suspend fun updateRunSession(runSession: RunSession)

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY timestamp DESC")
    fun getAllRunSessionsSortedByDate(email : String): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY timeInMilis DESC")
    fun getAllRunSessionsSortedByTimeInMilis(email : String): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY caloriesBurnt DESC")
    fun getAllRunSessionsSortedByCaloriesBurnt(email : String): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY avgSpeedInKMH DESC")
    fun getAllRunSessionsSortedByAvgSpeed(email : String): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY distanceInMeters DESC")
    fun getAllRunSessionsSortedByDistance(email : String): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY stepsPerSession DESC")
    fun getAllRunSessionsSortedBySteps(email : String): LiveData<List<RunSession>>

    @Query("SELECT * FROM runsession WHERE email = :email ORDER BY timestamp ASC LIMIT 1")
    fun getMostRecentRunSession(email : String): LiveData<RunSession>

    @Query("SELECT SUM(timeInMilis) FROM runsession WHERE email = :email")
    fun getTotalTimeInMilis(email : String): LiveData<Long>

    @Query("SELECT SUM(caloriesBurnt) FROM runsession WHERE email = :email")
    fun getTotalCaloriesBurnt(email : String): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM runsession WHERE email = :email")
    fun getTotalDistance(email : String): LiveData<Int>

    @Query("SELECT SUM(avgSpeedInKMH) FROM runsession WHERE email = :email")
    fun getTotalAvgSpeed(email : String): LiveData<Float>

    @Query("SELECT SUM(stepsPerSession) FROM runsession WHERE email = :email")
    fun getTotalSteps(email : String): LiveData<Float>
}