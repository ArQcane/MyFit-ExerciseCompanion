package com.example.myfit_exercisecompanion.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Camera
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentProfileBinding
import com.example.myfit_exercisecompanion.databinding.FragmentRunTrackerBinding
import com.example.myfit_exercisecompanion.other.Constants.ACTION_PAUSE_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.MAP_ZOOM
import com.example.myfit_exercisecompanion.other.Constants.POLYLINE_COLOR
import com.example.myfit_exercisecompanion.other.Constants.POLYLINE_WIDTH
import com.example.myfit_exercisecompanion.services.PolyLine
import com.example.myfit_exercisecompanion.services.TrackingService

import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunTrackerFragment : Fragment(R.layout.fragment_run_tracker) {


    private val viewModel: RunSessionViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<PolyLine>()

    private var map: GoogleMap? = null

    private var _binding: FragmentRunTrackerBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding.mapView.onCreate(savedInstanceState)
        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }


        binding.mapView.getMapAsync{
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints= it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else{
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polyLineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}