package com.example.myfit_exercisecompanion.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.myfit_exercisecompanion.repository.RunSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val runSessionRepository: RunSessionRepository
): ViewModel() {

    val totalTimeRun = runSessionRepository.getTotalTimeInMilis()
    val totalDistance = runSessionRepository.getTotalDistance()
    val totalCaloriesBurned = runSessionRepository.getTotalCaloriesBurnt()
    val totalAvgSpeed = runSessionRepository.getTotalAvgSpeed()
    val totalStepsTaken = runSessionRepository.getTotalSteps()

    val runsSortedByDate = runSessionRepository.getAllRunSessionsSortedByDate()

}