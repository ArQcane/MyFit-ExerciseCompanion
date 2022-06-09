package com.example.myfit_exercisecompanion.repository

import com.example.myfit_exercisecompanion.db.RunSessionDAO
import com.example.myfit_exercisecompanion.models.RunSession
import javax.inject.Inject

class RunSessionRepository @Inject constructor(
    val runSessionDAO: RunSessionDAO
){
    suspend fun insertRunSession(runSession : RunSession) = runSessionDAO.insertRunSession(runSession)

    suspend fun deleteRunSession(runSession : RunSession) = runSessionDAO.deleteRunSession(runSession)

    fun getAllRunSessionsSortedByDate() = runSessionDAO.getAllRunSessionsSortedByDate()

    fun getAllRunSessionsSortedByDistance() = runSessionDAO.getAllRunSessionsSortedByDistance()

    fun getAllRunSessionsSortedByTimeInMilis() = runSessionDAO.getAllRunSessionsSortedByTimeInMilis()

    fun getAllRunSessionsSortedByAvgSpeed() = runSessionDAO.getAllRunSessionsSortedByAvgSpeed()

    fun getAllRunSessionsSortedByCaloriesBurnt() = runSessionDAO.getAllRunSessionsSortedByCaloriesBurnt()

    fun getTotalAvgSpeed() = runSessionDAO.getTotalAvgSpeed()

    fun getTotalDistance() = runSessionDAO.getTotalDistance()

    fun getTotalCaloriesBurnt() = runSessionDAO.getTotalCaloriesBurnt()

    fun getTotalTimeInMilis() = runSessionDAO.getTotalTimeInMilis()

    // TODO: Need to Make a getTotalSteps fun and sort by steps fun
}