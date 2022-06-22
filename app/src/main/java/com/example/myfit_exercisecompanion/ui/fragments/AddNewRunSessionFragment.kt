package com.example.myfit_exercisecompanion.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
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
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.other.Constants.ACTION_STOP_SERVICE
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.services.TrackingService
import com.example.myfit_exercisecompanion.ui.activities.DetailsActivity
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddNewRunSessionFragment : Fragment(R.layout.fragment_add_new_run_session) {

    private val args: AddNewRunSessionFragmentArgs by navArgs()

    private var _binding: FragmentAddNewRunSessionBinding? = null

    private val viewModel: RunSessionViewModel by viewModels()

    private val authViewModel : AuthViewModel by viewModels()

    private var weight = 60.0

    private val binding get() = _binding!!

    private var _distance: Int = 0
    private var _timeTaken: Long = 0L
    private var _avgSpeed: Float = 0F
    private var _caloriesBurnt: Int = 0
    private var _stepsTaken: Int = 0
    private var _mapScreenShot: Bitmap? = null

    private val dateTimeStamp = Calendar.getInstance().timeInMillis

    private var email = ""


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
            CoroutineScope(Dispatchers.Main).launch{
                tvUsername.text = viewModel.getAuthUser()?.email
            }
        }


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

        viewModel.user.observe(viewLifecycleOwner) {
            it ?: return@observe run {
                Intent(requireContext(), DetailsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    requireActivity().startActivity(this)
                }
            }
            weight = it.weightInKilograms
        }
        viewModel.getAuthUser()?.let { email = it.email!! }
        viewModel.getAuthUser()
    }

    private fun publishRunToDb(){
        val title = binding.etTitleRun.text.toString()
        val runSession = RunSession(email, _mapScreenShot, title, dateTimeStamp, _avgSpeed, _distance, _timeTaken, _caloriesBurnt, _stepsTaken)
        viewModel.insertRunSession(runSession)
        findNavController().navigate(R.id.action_addNewRunSessionFragment_to_homeFragment)
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
}