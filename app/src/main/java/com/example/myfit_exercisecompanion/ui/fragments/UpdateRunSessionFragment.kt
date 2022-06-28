package com.example.myfit_exercisecompanion.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentUpdateRunSessionBinding
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class UpdateRunSessionFragment : Fragment(com.example.myfit_exercisecompanion.R.layout.fragment_update_run_session) {
    private val args by navArgs<UpdateRunSessionFragmentArgs>()

    private var _binding: FragmentUpdateRunSessionBinding? = null

    private val binding get() = _binding!!

    private val viewModel: RunSessionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentUpdateRunSessionBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.tilRunTitle.editText?.setText(args.currentRunSession.runSessionTitle)
        binding.tvUsername.text = args.currentRunSession.email
        val calendar = Calendar.getInstance().apply {
            timeInMillis = args.currentRunSession.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        binding.tvDate.text = "Posted on: ${dateFormat.format(calendar.time)}"

        Glide.with(this).load(args.currentRunSession.img).into(binding.ivMapImage)

        val avgSpeed = "Pace: ${args.currentRunSession.avgSpeedInKMH}km/h"
        binding.tvAvgSpeed.text = avgSpeed

        val distanceInKm = "Distance Travelled: ${args.currentRunSession.distanceInMeters / 1000f}km"
        binding.tvDistance.text = distanceInKm

        binding.tvTimeTaken.text = "Time: ${TrackingUtility.getFormattedStopWatchTime(args.currentRunSession.timeInMilis)}"

        val caloriesBurned = "Calories Burned: ${args.currentRunSession.caloriesBurnt}kcal"
        binding.tvCaloriesBurnt.text = caloriesBurned

        val stepsTaken = "${args.currentRunSession.stepsPerSession} steps"
        binding.tvStepsTaken.text = stepsTaken

        binding.btnUpdateRun.setOnClickListener {
            updateItem()
        }
        return view
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun updateItem(){
        val email = args.currentRunSession.email
        val img = args.currentRunSession.img
        val runSessionTitle = binding.tilRunTitle.editText?.text.toString()
        val timeStamp = args.currentRunSession.timestamp
        val avgSpeedInKMH = args.currentRunSession.avgSpeedInKMH
        val distanceInMeters = args.currentRunSession.distanceInMeters
        val timeInMilis = args.currentRunSession.timeInMilis
        val caloriesBurnt = args.currentRunSession.caloriesBurnt
        val stepsPerSession = args.currentRunSession.stepsPerSession

        if(inputCheck(runSessionTitle)){
            viewModel.updateRunSession(email!!, img!!, runSessionTitle, timeStamp, avgSpeedInKMH, distanceInMeters, timeInMilis, caloriesBurnt, stepsPerSession, args.currentRunSession.id)
            Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(com.example.myfit_exercisecompanion.R.id.action_updateRunSessionFragment_to_homeFragment)
        } else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(title: String): Boolean{
        return !(TextUtils.isEmpty(title))
    }

}