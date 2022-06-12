package com.example.myfit_exercisecompanion.ui.fragments

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentHomeBinding
import com.example.myfit_exercisecompanion.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.ui.LoginActivity
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), EasyPermissions.PermissionCallbacks {

    private val viewModel: RunSessionViewModel by viewModels()

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