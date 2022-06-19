package com.example.myfit_exercisecompanion.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentRunSessionDetailsBinding
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel


class RunSessionDetailsFragment : Fragment(R.layout.fragment_run_session_details) {
    private val args: RunSessionDetailsFragmentArgs by navArgs()
    lateinit var runSession: RunSession

    private var _binding: FragmentRunSessionDetailsBinding? = null

    private val viewModel: RunSessionViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentRunSessionDetailsBinding.inflate(inflater, container, false)

        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = args.itemId
        if (id > 0) {

        }
    }


}