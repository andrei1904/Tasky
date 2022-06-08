package com.example.tasky.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.tasky.databinding.ViewSearchBarBinding

abstract class SearchableFragment<T, VB: ViewBinding> : BaseFragment<VB>() {

    private lateinit var searchBinding: ViewSearchBarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)

        searchBinding = ViewSearchBarBinding.bind(binding.root)

        return rootView
    }
}