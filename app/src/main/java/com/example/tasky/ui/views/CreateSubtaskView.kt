package com.example.tasky.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.example.tasky.R
import com.example.tasky.databinding.ViewAddSubtaskBinding
import com.google.android.material.textfield.TextInputLayout

class CreateSubtaskView(context: Context) :
    ConstraintLayout(context, null, -1) {

    private val binding: ViewAddSubtaskBinding =
        ViewAddSubtaskBinding.inflate(LayoutInflater.from(context), this, true)

    private var isExapnded = true

    private var subtaskNumber = 0

    private lateinit var listener: CreateSubtaskViewListener

    private var textInputLayoutTitle: TextInputLayout
    private var textInputLayoutDescription: TextInputLayout

    init {
        binding.imageButtonShowHide.setOnClickListener {
            if (isExapnded) {
                hideDetails()
            } else {
                showDetails()
            }
        }

        initRemoveButton()

        textInputLayoutTitle = binding.textInputTitle
        textInputLayoutDescription = binding.textInputDescription
    }

    fun setSubtaskNumber(numberOfSubtasks: Int) {
        subtaskNumber = numberOfSubtasks

        binding.textViewSubtakName.text = subtaskNumber.toString()
    }

    private fun showDetails() {
        binding.groupSubtaskFields.visibility = View.VISIBLE
        binding.imageButtonShowHide.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_arrow_drop_up_24,
                context.theme
            )
        )
        isExapnded = !isExapnded
    }

    fun hideDetails() {
        binding.groupSubtaskFields.visibility = View.GONE
        binding.imageButtonShowHide.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_arrow_drop_down_24,
                context.theme
            )
        )
        isExapnded = !isExapnded
    }

    private fun initRemoveButton() {
        binding.imageButtonRemoveSubtask.setOnClickListener {
            listener.onRemoveClicked(subtaskNumber)
        }
    }

    fun setListener(listener: CreateSubtaskViewListener) {
        this.listener = listener
    }

    fun getTextInputLayoutTitle() : TextInputLayout {
        return textInputLayoutTitle
    }

    fun getTextInputLayoutDescription() : TextInputLayout {
        return textInputLayoutDescription
    }

    interface CreateSubtaskViewListener {
        fun onRemoveClicked(subtaskPosition: Int)
    }
}