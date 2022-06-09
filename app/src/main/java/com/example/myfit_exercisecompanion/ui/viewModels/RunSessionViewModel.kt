package com.example.myfit_exercisecompanion.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.myfit_exercisecompanion.repository.RunSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RunSessionViewModel @Inject constructor(
    val runSessionRepository: RunSessionRepository
): ViewModel() {
}