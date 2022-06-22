package com.example.myfit_exercisecompanion.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.myfit_exercisecompanion.repository.AuthRepository
import com.example.myfit_exercisecompanion.repository.RunSessionRepository
import com.example.myfit_exercisecompanion.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val runSessionRepository: RunSessionRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private fun getAuthUser() = authRepository.getAuthUser()

    val totalTimeRun = runSessionRepository.getTotalTimeInMilis(getAuthUser()!!.email!!)
    val totalDistance = runSessionRepository.getTotalDistance(getAuthUser()!!.email!!)
    val totalCaloriesBurned = runSessionRepository.getTotalCaloriesBurnt(getAuthUser()!!.email!!)
    val totalAvgSpeed = runSessionRepository.getTotalAvgSpeed(getAuthUser()!!.email!!)
    val totalStepsTaken = runSessionRepository.getTotalSteps(getAuthUser()!!.email!!)
    val runsSortedByDate = runSessionRepository.getAllRunSessionsSortedByDate(getAuthUser()!!.email!!)
}