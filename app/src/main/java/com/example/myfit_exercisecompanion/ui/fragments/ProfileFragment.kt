package com.example.myfit_exercisecompanion.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentProfileBinding
import com.example.myfit_exercisecompanion.models.User
import com.example.myfit_exercisecompanion.other.CustomMarkerView
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.example.myfit_exercisecompanion.other.createSnackBar
import com.example.myfit_exercisecompanion.ui.activities.LoginActivity
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.FoodListViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()

    private val viewModel: RunSessionViewModel by viewModels()

    private val foodListViewModel: FoodListViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private var user: User? = null

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
        viewModel.user.observe(viewLifecycleOwner){
            user = it
            Log.d("user value2:", it.toString())
            binding.tvProfileName.text = user?.username
            binding.tvEmailId.text = user?.email
            user?.profilePic.let { profilePic ->
                Picasso.with(requireContext()).load(profilePic).into(binding.profileImage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        binding.btnLogout.setOnClickListener {
            authViewModel.signOut()
            Intent(requireContext(), LoginActivity::class.java).let {
                requireActivity().apply {
                    startActivity(it)
                    finish()
                }
            }
        }
        binding.btnEditProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            findNavController().navigate(action)
        }
        binding.btnDeleteProfile.setOnClickListener {
            showDeleteAccountAlertDialog(requireContext())
        }
        Log.d("user value:" ,user.toString())
        viewModel.getCurrentUser()
    }

    private fun showDeleteAccountAlertDialog(context: Context) {
        val input = EditText(this.requireContext())
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Your Account?")
            .setMessage("Are you sure you want to delete your account? If so please enter password below")
            .setView(input)
            .setPositiveButton("Yes") { dialog, _ ->
                if (input.editableText.isNullOrEmpty()){
                    input.error = "Please enter password"
                }
                else{
                    val userPassword = input.text.toString()
                    val credential : AuthCredential = EmailAuthProvider.getCredential(user!!.email, userPassword)
                    authViewModel.getFirebaseUser()!!.reauthenticate(credential).addOnCompleteListener { credentials ->
                        if (credentials.isSuccessful){
                            authViewModel.deleteUserFromFirestoreDb(user!!.email)
                            authViewModel.getFirebaseUser()!!.delete().addOnCompleteListener {
                                viewModel.deleteAllRuns(user!!.email)
                                viewModel.deleteAllFoodItems(user!!.email)
                                if(it.isSuccessful){
                                    dialog.dismiss()
                                    authViewModel.signOut()
                                    Intent(requireContext(), LoginActivity::class.java).run {
                                        requireActivity().startActivity(this)
                                        requireActivity().finish()
                                    }
                                    Toast.makeText(requireContext(), "Successfully deleted your account", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(requireContext(), it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(requireContext(), credentials.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}