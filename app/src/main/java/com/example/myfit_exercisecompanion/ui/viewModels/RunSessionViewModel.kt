package com.example.myfit_exercisecompanion.ui.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.other.SortTypes
import com.example.myfit_exercisecompanion.repository.RunSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunSessionViewModel @Inject constructor(
    val runSessionRepository: RunSessionRepository
): ViewModel() {

    private val runsSortedByDate = runSessionRepository.getAllRunSessionsSortedByDate()
    private val runsSortedByDistance = runSessionRepository.getAllRunSessionsSortedByDistance()
    private val runsSortedByCaloriesBurnt = runSessionRepository.getAllRunSessionsSortedByCaloriesBurnt()
    private val runsSortedByTimeInMilis = runSessionRepository.getAllRunSessionsSortedByTimeInMilis()
    private val runsSortedByAverageSpeed = runSessionRepository.getAllRunSessionsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<RunSession>>()

    var sortTypes = SortTypes.DATE

    init {
        runs.addSource(runsSortedByDate) { result ->
            if(sortTypes == SortTypes.DATE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByDistance) { result ->
            if(sortTypes == SortTypes.DISTANCE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByCaloriesBurnt) { result ->
            if(sortTypes == SortTypes.CALORIES_BURNT){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByTimeInMilis) { result ->
            if(sortTypes == SortTypes.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByAverageSpeed) { result ->
            if(sortTypes == SortTypes.AVG_SPEED){
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortTypes: SortTypes) = when(sortTypes){
        SortTypes.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortTypes.RUNNING_TIME -> runsSortedByTimeInMilis.value?.let { runs.value = it }
        SortTypes.AVG_SPEED -> runsSortedByAverageSpeed.value?.let { runs.value = it }
        SortTypes.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortTypes.CALORIES_BURNT -> runsSortedByCaloriesBurnt.value?.let { runs.value = it }
    }.also {
        this.sortTypes = sortTypes
    }

    fun insertRunSession(runSession: RunSession) = viewModelScope.launch {
        runSessionRepository.insertRunSession(runSession)
    }


}