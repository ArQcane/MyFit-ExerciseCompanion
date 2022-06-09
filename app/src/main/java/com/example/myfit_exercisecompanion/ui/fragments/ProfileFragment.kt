package com.example.myfit_exercisecompanion.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel: StatisticsViewModel by viewModels()

}