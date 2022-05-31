package com.example.tasky.ui.activites

import android.os.Bundle
import com.example.tasky.R
import com.example.tasky.databinding.ActivityMainBinding
import com.example.tasky.ui.fragments.ProfileFragment
import com.example.tasky.ui.fragments.TasksFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getFragmentNavigation().replaceFragment(TasksFragment())

        bottomNavigationCallback()
    }

    private fun bottomNavigationCallback() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_tasks -> {
                    getFragmentNavigation().replaceFragment(TasksFragment())
                    true
                }
                R.id.page_profile -> {
                    getFragmentNavigation().replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}