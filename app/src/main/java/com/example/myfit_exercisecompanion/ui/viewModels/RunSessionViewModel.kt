package com.example.myfit_exercisecompanion.ui.viewModels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.models.User
import com.example.myfit_exercisecompanion.other.SortTypes
import com.example.myfit_exercisecompanion.repository.AuthRepository
import com.example.myfit_exercisecompanion.repository.RunSessionRepository
import com.example.myfit_exercisecompanion.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunSessionViewModel @Inject constructor(
    private val runSessionRepository: RunSessionRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _userLoggedIn = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _userLoggedIn

    private val runsSortedByDate = runSessionRepository.getAllRunSessionsSortedByDate(getAuthUser()?.email!!)
    private val runsSortedByDistance = runSessionRepository.getAllRunSessionsSortedByDistance(getAuthUser()?.email!!)
    private val runsSortedByCaloriesBurnt = runSessionRepository.getAllRunSessionsSortedByCaloriesBurnt(getAuthUser()?.email!!)
    private val runsSortedByTimeInMilis = runSessionRepository.getAllRunSessionsSortedByTimeInMilis(getAuthUser()?.email!!)
    private val runsSortedByAverageSpeed = runSessionRepository.getAllRunSessionsSortedByAvgSpeed(getAuthUser()?.email!!)
    private val runsSortedByStepsTaken = runSessionRepository.getAllRunSessionsSortedBySteps(getAuthUser()?.email!!)


    val runs = MediatorLiveData<List<RunSession>>()

    var sortTypes = SortTypes.DATE

    init {
        getAuthUser()?.email?.let { user ->
            runs.addSource(runsSortedByDate) { result ->
                if (sortTypes == SortTypes.DATE) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByDistance) { result ->
                if (sortTypes == SortTypes.DISTANCE) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByCaloriesBurnt) { result ->
                if (sortTypes == SortTypes.CALORIES_BURNT) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByTimeInMilis) { result ->
                if (sortTypes == SortTypes.RUNNING_TIME) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByAverageSpeed) { result ->
                if (sortTypes == SortTypes.AVG_SPEED) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByStepsTaken) { result ->
                if (sortTypes == SortTypes.STEPS_TAKEN) {
                    result?.let { runs.value = it }
                }
            }
        }
    }

    fun sortRuns(sortTypes: SortTypes) {
        getAuthUser()?.let { user ->
            when (sortTypes) {
                SortTypes.DATE -> runsSortedByDate.value?.let { runs.value = it }
                SortTypes.RUNNING_TIME -> runsSortedByTimeInMilis.value?.let { runs.value = it }
                SortTypes.AVG_SPEED -> runsSortedByAverageSpeed.value?.let { runs.value = it }
                SortTypes.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
                SortTypes.CALORIES_BURNT -> runsSortedByCaloriesBurnt.value?.let { runs.value = it }
                SortTypes.STEPS_TAKEN -> runsSortedByStepsTaken.value?.let { runs.value = it }
            }.also {
                this.sortTypes = sortTypes
            }
        }
    }

    fun getAuthUser() = authRepository.getAuthUser()

    fun insertRunSession(runSession: RunSession) = viewModelScope.launch {
        runSessionRepository.insertRunSession(runSession)
    }

    fun deleteRunSession(runSession: RunSession) = viewModelScope.launch {
        runSessionRepository.deleteRunSession(runSession)
    }

    fun updateRunSession(email: String, img: Bitmap, runSessionTitle: String, timestamp: Long, avgSpeedInKMH: Float, distanceInMeters: Int, timeInMilis: Long, caloriesBurnt: Int, stepsPerSession: Int, id: Int) = viewModelScope.launch {
        runSessionRepository.updateRunSession(email, img, runSessionTitle, timestamp, avgSpeedInKMH, distanceInMeters, timeInMilis, caloriesBurnt, stepsPerSession, id)
    }
}