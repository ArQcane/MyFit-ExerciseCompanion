package com.example.myfit_exercisecompanion.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.icu.number.NumberFormatter.with
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentEditProfileBinding
import com.example.myfit_exercisecompanion.models.User
import com.example.myfit_exercisecompanion.other.Constants.REQUEST_CODE_CHOOSE_PROFILE_PICTURE
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_profile.*

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private lateinit var binding: FragmentEditProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: RunSessionViewModel by viewModels()
    private var user: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        viewModel.user.observe(viewLifecycleOwner) {
            user = it
            it ?: return@observe
            binding.usernameInput.editText?.setText(user!!.username)
            binding.heightInput.editText?.setText((user!!.heightInMetres * 100).toString())
            binding.weightInput.editText?.setText(user!!.weightInKilograms.toString())
            user!!.profilePic?.let { profilePic ->
                Picasso.with(requireContext()).load(profilePic).into(binding.profileImage)
            }
        }
        authViewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthViewModel.AuthState.Success -> {
                    Snackbar.make(getView()!!,"Successfully updated the profile" , Snackbar.LENGTH_LONG).show();
                }
                is AuthViewModel.AuthState.FireBaseFailure -> {
                    Snackbar.make(getView()!!,it.message ?: "Unknown error has occurred" , Snackbar.LENGTH_LONG).show();
                }
                is AuthViewModel.AuthState.InvalidUsername -> {
                    binding.usernameInput.isErrorEnabled = true
                    binding.usernameInput.error = it.message
                }
                is AuthViewModel.AuthState.InvalidHeight -> {
                    binding.heightInput.isErrorEnabled = true
                    binding.heightInput.error = it.message
                }
                is AuthViewModel.AuthState.InvalidWeight -> {
                    binding.weightInput.isErrorEnabled = true
                    binding.weightInput.error = it.message
                }
                else -> {
                }
            }
        }
        viewModel.getCurrentUser()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.usernameInput.editText?.addTextChangedListener {
            authViewModel.username = it.toString()
            binding.usernameInput.isErrorEnabled = false
        }
        binding.heightInput.editText?.addTextChangedListener {
            authViewModel.height = it.toString()
            binding.heightInput.isErrorEnabled = false
        }
        binding.weightInput.editText?.addTextChangedListener {
            authViewModel.weight = it.toString()
            binding.weightInput.isErrorEnabled = false
        }
        binding.btnSubmit.setOnClickListener { authViewModel.updateUser() }
        binding.profileImage.setOnClickListener {
            startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_CODE_CHOOSE_PROFILE_PICTURE
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        view?.let { onViewCreated(it, null) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_PROFILE_PICTURE &&
            resultCode == Activity.RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            binding.profileImage.setImageURI(data.data)
            authViewModel.uri = data.data
        }
    }
}