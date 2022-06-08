package com.example.myfit_exercisecompanion.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.RunTrackerConfigs.MapPresenter
import com.example.myfit_exercisecompanion.RunTrackerConfigs.Ui
import com.example.myfit_exercisecompanion.databinding.FragmentRunTrackerBinding
import com.example.myfit_exercisecompanion.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class RunTrackerFragment : Fragment(R.layout.fragment_run_tracker), OnMapReadyCallback {
    private var _presenter : MapPresenter? = null
    private lateinit var map: GoogleMap
    private var _binding: FragmentRunTrackerBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private val presenter get() = _presenter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRunTrackerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _presenter = (activity as MainActivity).presenter
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnStartStop.setOnClickListener {
            if (binding.btnStartStop.text == getString(R.string.start_label)) {
                startTracking()
                binding.btnStartStop.setText(R.string.stop_label)
            } else {
                stopTracking()
                binding.btnStartStop.setText(R.string.start_label)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
//        presenter = (activity as MainActivity).presenter

        presenter?.ui?.observe(this) { ui ->
            updateUi(ui)
        }

        presenter?.onMapLoaded()
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun startTracking() {
        binding.container.txtPace.text = ""
        binding.container.txtDistance.text = ""
        binding.container.txtTime.base = SystemClock.elapsedRealtime()
        binding.container.txtTime.start()
        map.clear()
        Log.d("type:", "${binding.container.txtTime.base}")

        presenter?.startTracking()
    }

    private fun stopTracking() {
        presenter?.stopTracking()
        binding.container.txtTime.stop()
    }

    @SuppressLint("MissingPermission")
    private fun updateUi(ui: Ui) {
        if (ui.currentLocation != null && ui.currentLocation != map.cameraPosition.target) {
            map.isMyLocationEnabled = true
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(ui.currentLocation, 14f))
        }
        binding.container.txtDistance.text = ui.formattedDistance
        binding.container.txtPace.text = ui.formattedPace
        drawRoute(ui.userPath)
    }

    private fun drawRoute(locations: List<LatLng>) {
        val polylineOptions = PolylineOptions()

        map.clear()

        val points = polylineOptions.points
        points.addAll(locations)

        map.addPolyline(polylineOptions)
    }
}