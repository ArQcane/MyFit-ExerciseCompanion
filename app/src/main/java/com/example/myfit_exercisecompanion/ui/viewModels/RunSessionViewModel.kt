package com.example.myfit_exercisecompanion.ui.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.models.User
import com.example.myfit_exercisecompanion.other.SortTypes
import com.example.myfit_exercisecompanion.repository.AuthRepository
import com.example.myfit_exercisecompanion.repository.RunSessionRepository
import com.example.myfit_exercisecompanion.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
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

    fun getCurrentUser() {
        viewModelScope.launch {
            _userLoggedIn.postValue(userRepository.getCurrentUser())
        }
    }

    fun runsSortedByDate(email: String) = runSessionRepository.getAllRunSessionsSortedByDate(email)
    fun runsSortedByDistance(email: String) = runSessionRepository.getAllRunSessionsSortedByDistance(email)
    fun runsSortedByCaloriesBurnt(email: String) = runSessionRepository.getAllRunSessionsSortedByCaloriesBurnt(email)
    fun runsSortedByTimeInMilis(email: String) = runSessionRepository.getAllRunSessionsSortedByTimeInMilis(email)
    fun runsSortedByAverageSpeed(email: String) = runSessionRepository.getAllRunSessionsSortedByAvgSpeed(email)
    fun runsSortedByStepsTaken(email: String) = runSessionRepository.getAllRunSessionsSortedBySteps(email)


    val runs = MediatorLiveData<List<RunSession>>()

    var sortTypes = SortTypes.DATE

    init {
        Timber.d("user email: ${getAuthUser()?.email!!}")
        getAuthUser()?.let { user ->
            runs.addSource(runsSortedByDate(user.email!!)) { result ->
                if (sortTypes == SortTypes.DATE) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByDistance(user.email!!)) { result ->
                if (sortTypes == SortTypes.DISTANCE) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByCaloriesBurnt(user.email!!)) { result ->
                if (sortTypes == SortTypes.CALORIES_BURNT) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByTimeInMilis(user.email!!)) { result ->
                if (sortTypes == SortTypes.RUNNING_TIME) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByAverageSpeed(user.email!!)) { result ->
                if (sortTypes == SortTypes.AVG_SPEED) {
                    result?.let { runs.value = it }
                }
            }
            runs.addSource(runsSortedByStepsTaken(user.email!!)) { result ->
                if (sortTypes == SortTypes.STEPS_TAKEN) {
                    result?.let { runs.value = it }
                }
            }
        }


    }

    fun sortRuns(sortTypes: SortTypes) {
        getAuthUser()?.let { user ->
            when (sortTypes) {
                SortTypes.DATE -> runsSortedByDate(user.email!!).value?.let { runs.value = it }
                SortTypes.RUNNING_TIME -> runsSortedByTimeInMilis(user.email!!).value?.let { runs.value = it }
                SortTypes.AVG_SPEED -> runsSortedByAverageSpeed(user.email!!).value?.let { runs.value = it }
                SortTypes.DISTANCE -> runsSortedByDistance(user.email!!).value?.let { runs.value = it }
                SortTypes.CALORIES_BURNT -> runsSortedByCaloriesBurnt(user.email!!).value?.let { runs.value = it }
                SortTypes.STEPS_TAKEN -> runsSortedByStepsTaken(user.email!!).value?.let { runs.value = it }
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

    fun updateRunSession(runSession: RunSession) = viewModelScope.launch {
        runSessionRepository.updateRunSession(runSession)
    }
}