package com.example.tasky.ui.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tasky.R
import com.example.tasky.navigation.FragmentNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private lateinit var fragmentNavigation: FragmentNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        fragmentNavigation = FragmentNavigation(this)
    }

    fun getFragmentNavigation():FragmentNavigation{
        return fragmentNavigation
    }
}
