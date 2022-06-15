package com.example.myfit_exercisecompanion.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.adapters.RunSessionAdapter
import com.example.myfit_exercisecompanion.databinding.FragmentHomeBinding
import com.example.myfit_exercisecompanion.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.myfit_exercisecompanion.other.SortTypes
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), EasyPermissions.PermissionCallbacks {

    private val viewModel: RunSessionViewModel by viewModels()

    private lateinit var runAdapter: RunSessionAdapter

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

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

        when(viewModel.sortTypes){
            SortTypes.DATE -> binding.spFilter.setSelection(0)
            SortTypes.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortTypes.DISTANCE -> binding.spFilter.setSelection(2)
            SortTypes.AVG_SPEED -> binding.spFilter.setSelection(3)
            SortTypes.CALORIES_BURNT -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> viewModel.sortRuns(SortTypes.DATE)
                    1 -> viewModel.sortRuns(SortTypes.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortTypes.DISTANCE)
                    3 -> viewModel.sortRuns(SortTypes.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortTypes.CALORIES_BURNT)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunSessionAdapter()
        adapter = runAdapter
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
}