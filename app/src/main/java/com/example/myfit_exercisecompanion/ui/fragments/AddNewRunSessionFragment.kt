package com.example.myfit_exercisecompanion.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentAddNewRunSessionBinding
import com.example.myfit_exercisecompanion.databinding.FragmentProfileBinding
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.other.Constants
import com.example.myfit_exercisecompanion.other.Constants.ACTION_STOP_SERVICE
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.services.TrackingService
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddNewRunSessionFragment : Fragment(R.layout.fragment_add_new_run_session) {

    private val args: AddNewRunSessionFragmentArgs by navArgs()

    private var _binding: FragmentAddNewRunSessionBinding? = null

    private val viewModel: RunSessionViewModel by viewModels()

    private val binding get() = _binding!!

    private var _distance: Int = 0
    private var _timeTaken: Long = 0L
    private var _avgSpeed: Float = 0F
    private var _caloriesBurnt: Int = 0
    private var _stepsTaken: Int = 0
    private var _mapScreenShot: Bitmap? = null

    private val dateTimeStamp = Calendar.getInstance().timeInMillis


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewRunSessionBinding.inflate(inflater, container, false)

        val distance = args.distance
        _distance = distance
        val timeTaken = args.timeTaken
        _timeTaken = timeTaken
        val avgSpeed = args.averageSpeed
        _avgSpeed = avgSpeed
        val caloriesBurnt = args.caloriesBurnt
        _caloriesBurnt = caloriesBurnt
        val stepsTaken = args.stepsCounted
        _stepsTaken = stepsTaken

        val mapScreenShot = args.mapScreenShot
        _mapScreenShot = mapScreenShot

        Timber.d("image: ${mapScreenShot}")

        Glide.with(this).load(mapScreenShot).into(binding.ivMapImage)

        binding.apply {
            ivMapImage.setImageResource(R.drawable.ic_baseline_wifi_protected_setup_24)
            tvDistance.text = TrackingUtility.getFormattedLiveDistance(_distance.toFloat() / 1000f)
            tvTimeTaken.text = TrackingUtility.getFormattedStopwatchTime(timeTaken)
            tvAvgSpeed.text = "${avgSpeed}km/h"
            tvCaloriesBurnt.text = "${caloriesBurnt}Kcal"
            tvStepsTaken.text = "${stepsTaken}steps"
            val calendar = Calendar.getInstance().timeInMillis
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar)
        }

        Timber.d("Values :" +
                "${distance}, ${timeTaken}, ${avgSpeed}, $caloriesBurnt, $stepsTaken")


        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPublishRun.setOnClickListener {
            if(binding.etTitleRun.text?.isNotEmpty() == true){
                publishRunToDb()
            }
            else(
                Snackbar.make(requireView(), "Please Input a title for your run", Snackbar.LENGTH_LONG).show()
            )
        }
    }

    private fun publishRunToDb(){
        val title = binding.etTitleRun.text.toString()
        val runSession = RunSession(_mapScreenShot, title, dateTimeStamp, _avgSpeed, _distance, _timeTaken, _caloriesBurnt, _stepsTaken)
        viewModel.insertRunSession(runSession)
        findNavController().navigate(R.id.action_addNewRunSessionFragment_to_homeFragment)
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

//    private fun endRunAndSaveToDb(){
//            val runSession = RunSession(bmp, dateTimeStamp, averageSpeed, distanceInMeters, curTimeInMilis, caloriesBurned, liveStepsCounted)
//            viewModel.insertRunSession(runSession)
//            Snackbar.make(
//                requireView().rootView,
//                "Run saved Successfully",
//                Snackbar.LENGTH_LONG
//            ).show()
//        }
//    }
}