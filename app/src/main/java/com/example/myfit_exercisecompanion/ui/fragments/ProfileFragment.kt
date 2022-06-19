package com.example.myfit_exercisecompanion.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentHomeBinding
import com.example.myfit_exercisecompanion.databinding.FragmentProfileBinding
import com.example.myfit_exercisecompanion.other.CustomMarkerView
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.ui.LoginActivity
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: StatisticsViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopwatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10f)/ 10f
                val totalDistanceString = "${totalDistance}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalCalories = "${it}kcal"
                binding.tvTotalCalories.text = totalCalories
            }
        })
        viewModel.totalStepsTaken.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalStepsTaken = "${it}steps"
                binding.tvTotalSteps.text = totalStepsTaken
            }
        })
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.GREEN
                    color = ContextCompat.getColor(requireContext(), R.color.colorDarkGreen)
                }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                binding.barChart.invalidate()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()

        val userId = activity?.intent?.getStringExtra("user_id")
        val emailId = activity?.intent?.getStringExtra("email_id")



        binding.tvUserId.text = "User ID :: ${authViewModel.getCurrentUser()}"
        binding.tvEmailId.text = "Email ID :: $emailId"

        binding.btnLogout.setOnClickListener {
            authViewModel.signOut()
            Intent(requireContext(), LoginActivity::class.java).let {
                requireActivity().apply {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private fun setupBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.GREEN
            textColor = Color.GREEN
            setDrawGridLines(true)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.DKGRAY
            textColor = Color.DKGRAY
            setDrawGridLines(true)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.DKGRAY
            textColor = Color.DKGRAY
            setDrawGridLines(true)
        }
        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val userId = activity?.intent?.getStringExtra("user_id")
//        val emailId = activity?.intent?.getStringExtra("email_id")
//
//        binding.tvUserId2.text = "User ID :: $userId"
//        binding.tvEmailId2.text = "Email ID :: $emailId"
//
//        binding.btnLogout2.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//
//            startActivity(Intent(activity, LoginActivity::class.java))
//        }
//    }
}