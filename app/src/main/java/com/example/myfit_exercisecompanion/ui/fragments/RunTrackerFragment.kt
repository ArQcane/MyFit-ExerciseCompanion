package com.example.myfit_exercisecompanion.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentRunTrackerBinding
import com.example.myfit_exercisecompanion.other.Constants.ACTION_PAUSE_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.ACTION_STOP_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.MAP_ZOOM
import com.example.myfit_exercisecompanion.other.Constants.POLYLINE_COLOR
import com.example.myfit_exercisecompanion.other.Constants.POLYLINE_WIDTH
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.services.Polyline
import com.example.myfit_exercisecompanion.services.TrackingService
import com.example.myfit_exercisecompanion.ui.DetailsActivity
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round

const val CANCEL_TRACKING_DIALOG_TAG = "CancelDialog"

@AndroidEntryPoint
class RunTrackerFragment : Fragment(R.layout.fragment_run_tracker) {

    private val viewModel: RunSessionViewModel by viewModels()


    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var curTimeInMilis = 0L

    private var liveDistance = 0f

    private var liveCaloriesBurnt = 0

    private var finalCaloriesBurned = 0

    private var liveStepsCounted = -1

    private var finalAverageSpeed = 0F

    private var weight = 80.0

    private var menu: Menu? = null

    private var _binding: FragmentRunTrackerBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentRunTrackerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mapView.onCreate(savedInstanceState)
            btnToggleRun.setOnClickListener {
                toggleRun()
            }

            btnFinishRun.setOnClickListener {
                zoomToSeeWholeTrack()
                sendArgsToAddNewRunFragment()
            }

            if(savedInstanceState != null){
                val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                    CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?
                cancelTrackingDialog?.setYesListener {
                    stopRun()
                }
            }

//            btnFinishRun.setOnClickListener {
//
//                endRunAndSaveToDb()
//            }


            mapView.getMapAsync {
                map = it
                addAllPolylines()
            }
            subscribeToObservers()
        }


    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMilis.observe(viewLifecycleOwner, Observer {
            curTimeInMilis = it
            val formattedTime = TrackingUtility.getFormattedStopwatchTime(curTimeInMilis)
            binding.container.tvTimer.text = formattedTime
        })
        TrackingService.liveDistance.observe(viewLifecycleOwner, Observer {
            liveDistance = it
            val formattedDistance = TrackingUtility.getFormattedLiveDistance(liveDistance)
            binding.container.tvDistance.text = formattedDistance
        })
        TrackingService.liveCaloriesBurnt.observe(viewLifecycleOwner, Observer {
            liveCaloriesBurnt = it
            binding.container.tvCaloriesBurnt.text = "${liveCaloriesBurnt}Kcal"
        })
        TrackingService.liveSteps.observe(viewLifecycleOwner, Observer {
            liveStepsCounted = it
            binding.container.tvStepsTravelled.text = "${liveStepsCounted} Steps"
        })

        viewModel.user.observe(viewLifecycleOwner) {
            it ?: return@observe run {
                Intent(requireContext(), DetailsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    requireActivity().startActivity(this)
                }
            }
            weight = it.weightInKG
        }
    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(curTimeInMilis > 0L){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog(){
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)

    }

    private fun stopRun(){
        binding.container.tvTimer.text = "00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_runTrackerFragment_to_homeFragment)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        binding.apply {
            if (!isTracking && curTimeInMilis > 0L) {
                btnToggleRun.text = "Resume"
                btnFinishRun.visibility = View.VISIBLE
            } else if (isTracking) {
                btnToggleRun.text = "Stop"
                menu?.getItem(0)?.isVisible = true
                btnFinishRun.visibility = View.GONE
            }
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun sendArgsToAddNewRunFragment(){
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val averageSpeed = round((distanceInMeters / 1000f) / (curTimeInMilis / 1000f / 60 / 60) * 10) / 10f
            finalAverageSpeed = averageSpeed
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            finalCaloriesBurned = caloriesBurned
            val action = RunTrackerFragmentDirections.actionRunTrackerFragmentToAddNewRunSessionFragment(mapScreenShot = bmp, timeTaken = curTimeInMilis, distance = distanceInMeters, averageSpeed = averageSpeed, caloriesBurnt = caloriesBurned, stepsCounted = liveStepsCounted)
            findNavController().navigate(action)
        }
    }


    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }
}