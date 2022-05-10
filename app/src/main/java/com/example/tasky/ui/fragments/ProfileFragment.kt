package com.example.tasky.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tasky.databinding.FragmnetProfileBinding
import com.example.tasky.ui.activites.LoginActivity
import com.example.tasky.ui.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmnetProfileBinding? = null

    private val binding get() = _binding!!

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmnetProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButton()
    }

    private fun initButton() {
        binding.buttonLogout.setOnClickListener {
            loginViewModel.onSignOut()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}