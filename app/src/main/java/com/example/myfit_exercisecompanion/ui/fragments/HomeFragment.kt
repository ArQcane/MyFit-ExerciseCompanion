package com.example.myfit_exercisecompanion.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentHomeBinding
import com.example.myfit_exercisecompanion.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(R.layout.fragment_home) {

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
        val userId = activity?.intent?.getStringExtra("user_id")
        val emailId = activity?.intent?.getStringExtra("email_id")

        binding.tvUserId2.text = "User ID :: $userId"
        binding.tvEmailId2.text = "Email ID :: $emailId"

        binding.btnLogout2.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
}