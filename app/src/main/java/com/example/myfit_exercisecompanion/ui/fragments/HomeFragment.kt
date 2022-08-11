package com.example.myfit_exercisecompanion.ui.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.adapters.HomeRunAdapter
import com.example.myfit_exercisecompanion.databinding.FragmentHomeBinding
import com.example.myfit_exercisecompanion.models.User
import com.example.myfit_exercisecompanion.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.myfit_exercisecompanion.other.CustomMarkerView
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.round


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), EasyPermissions.PermissionCallbacks {

    private val viewModel: RunSessionViewModel by viewModels()

    private lateinit var runAdapter: HomeRunAdapter

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setupRecyclerView()
        subscribeToObservers()
        setupBarChart()

        binding.btnRunsList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_runsListFragment)
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val runSession = runAdapter.differ.currentList[position]
                viewModel.deleteRunSession(runSession)
                Snackbar.make(view, "Successfully deleted run session", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.insertRunSession(runSession)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvRuns)
        }
        viewModel.getCurrentUser()
    }



    private fun setupRecyclerView() = binding.rvRuns.apply {
        runAdapter = HomeRunAdapter()
        adapter = runAdapter
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            if(it.size != 0){
                binding.rvRuns.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                binding.ivEmpty.visibility = View.GONE
            }
            else{
                binding.rvRuns.visibility = View.INVISIBLE
                binding.ivEmpty.visibility = View.VISIBLE
                binding.ivEmpty.visibility = View.VISIBLE
            }
        })
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "This App needs location permissions to work properly." ,
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else{
            EasyPermissions.requestPermissions(
                this,
                "This App needs location permissions to work properly." ,
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun subscribeToObservers() {
        statisticsViewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
        statisticsViewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistance}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        })
        statisticsViewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })
        statisticsViewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalCalories = "${it}kcal"
                binding.tvTotalCalories.text = totalCalories
            }
        })
        statisticsViewModel.totalStepsTaken.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalStepsTaken = "${it}steps"
                binding.tvTotalSteps.text = totalStepsTaken
            }
        })
        statisticsViewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.BLUE
                    valueTextSize = 24.0f
                    color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                binding.barChart.invalidate()
            }
        })
    }
    private fun setupBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.GREEN
            textColor = Color.GREEN
            gridColor = Color.parseColor("#FF7F3F")
            setDrawGridLines(true)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.DKGRAY
            textColor = Color.DKGRAY
            gridColor = Color.parseColor("#FF7F3F")
            setDrawGridLines(true)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.DKGRAY
            textColor = Color.DKGRAY
            gridColor = Color.parseColor("#FF7F3F")
            setDrawGridLines(true)
        }
        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            description.textSize = 20.0f
            legend.isEnabled = false
        }
    }
}