package com.example.tasky.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.databinding.ViewTopBarBinding
import com.google.android.material.textfield.TextInputLayout

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected var viewBinding: VB? = null

    protected val binding get() = viewBinding!!

    protected var rootView: View? = null

    protected var isFirstLoaded = false

    private lateinit var topBarBinding: ViewTopBarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreateViewBinding(inflater, container)
        topBarBinding = ViewTopBarBinding.bind(binding.root)

        initRightIcon()
        initLeftIcon()
        initTitle()

        return rootView
    }

    private fun initRightIcon() {

        val icons = getRightIcons()
        for (icon in icons) {
            if (icon == null) {
                return
            }

            when (icon.type) {
                IconType.ADD_ICON -> initIcon(icon, topBarBinding.imageButtonAdd)
                IconType.CHECK_ICON -> initIcon(icon, topBarBinding.imageButtonCreate)
                IconType.NEXT_ICON -> initIcon(icon, topBarBinding.imageButtonNext)
                IconType.EDIT_ICON -> initIcon(icon, topBarBinding.imageButtonEdit)
            }
        }
    }

    protected fun rightIconsChanged() {
        initRightIcon()
    }

    private fun initLeftIcon() {

        val icon = getLeftIcon() ?: return
        initIcon(icon, topBarBinding.imageButtonBack)
    }

    private fun initTitle() {
        topBarBinding.textViewTopBarTitle.text = getTitle()
    }

    private fun initIcon(icon: Icon, view: View) {
        view.visibility = icon.visibility
//        view.setBackgroundResource(icon.id)
        setClickListenerForButtons(icon, view)
    }

    private fun setClickListenerForButtons(icon: Icon, view: View) {
        view.setOnClickListener { event ->
            icon.listener?.onClick(event)
        }
    }

    abstract fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?)

    abstract fun getRightIcons(): List<Icon?>

    abstract fun getLeftIcon(): Icon?

    abstract fun getTitle(): String
}