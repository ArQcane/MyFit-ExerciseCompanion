package com.example.myfit_exercisecompanion.repository

import android.graphics.Bitmap
import com.example.myfit_exercisecompanion.db.RunSessionDAO
import com.example.myfit_exercisecompanion.models.RunSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunSessionRepository @Inject constructor(
    val runSessionDAO: RunSessionDAO
){


    suspend fun insertRunSession(runSession : RunSession) {
        withContext(Dispatchers.IO){
            runSessionDAO.insertRunSession(runSession)
        }
    }

    suspend fun deleteRunSession(runSession : RunSession) {
        withContext(Dispatchers.IO){
            runSessionDAO.deleteRunSession(runSession)
        }
    }

    suspend fun updateRunSession(email: String, img: Bitmap, runSessionTitle: String, timestamp: Long, avgSpeedInKMH: Float, distanceInMeters: Int, timeInMilis: Long, caloriesBurnt: Int, stepsPerSession: Int, id: Int) = runSessionDAO.updateRunSession(email, img, runSessionTitle, timestamp, avgSpeedInKMH, distanceInMeters, timeInMilis, caloriesBurnt, stepsPerSession, id)

    fun getAllRunSessionsSortedByDate(email: String) = runSessionDAO.getAllRunSessionsSortedByDate(email)

    fun getAllRunSessionsSortedByDistance(email: String) = runSessionDAO.getAllRunSessionsSortedByDistance(email)

    fun getAllRunSessionsSortedByTimeInMilis(email: String) = runSessionDAO.getAllRunSessionsSortedByTimeInMilis(email)

    fun getAllRunSessionsSortedByAvgSpeed(email: String) = runSessionDAO.getAllRunSessionsSortedByAvgSpeed(email)

    fun getAllRunSessionsSortedByCaloriesBurnt(email: String) = runSessionDAO.getAllRunSessionsSortedByCaloriesBurnt(email)

    fun getAllRunSessionsSortedBySteps(email: String) = runSessionDAO.getAllRunSessionsSortedBySteps(email)

    fun getTotalAvgSpeed(email: String) = runSessionDAO.getTotalAvgSpeed(email)

    fun getTotalDistance(email: String) = runSessionDAO.getTotalDistance(email)

    fun getTotalCaloriesBurnt(email: String) = runSessionDAO.getTotalCaloriesBurnt(email)

    fun getTotalTimeInMilis(email: String) = runSessionDAO.getTotalTimeInMilis(email)

    fun getTotalSteps(email: String) = runSessionDAO.getTotalSteps(email)
}