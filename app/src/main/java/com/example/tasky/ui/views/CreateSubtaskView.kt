package com.example.tasky.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.tasky.R
import com.example.tasky.databinding.ViewAddSubtaskBinding
import com.google.android.material.textfield.TextInputLayout

class CreateSubtaskView(context: Context) :
    ConstraintLayout(context, null, -1) {

    private val binding: ViewAddSubtaskBinding =
        ViewAddSubtaskBinding.inflate(LayoutInflater.from(context), this, true)

    private var isExpanded = true

    private var subtaskNumber = 0

    private lateinit var listener: CreateSubtaskViewListener

    private var textInputLayoutTitle: TextInputLayout
    private var textInputLayoutDescription: TextInputLayout
    private var textInputLayoutDifficulty: TextInputLayout

    init {
        binding.imageButtonShowHide.setOnClickListener {
            if (isExpanded) {
                hideDetails()
            } else {
                showDetails()
            }
        }

        initRemoveButton()

        textInputLayoutTitle = binding.textInputTitle
        textInputLayoutDescription = binding.textInputDescription
        textInputLayoutDifficulty = binding.textInputDifficulty
    }

    fun setSubtaskNumber(numberOfSubtasks: Int) {
        subtaskNumber = numberOfSubtasks

        val number = subtaskNumber + 1
        binding.textViewSubtakName.text = number.toString()
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
        isExpanded = !isExpanded
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
        isExpanded = !isExpanded
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

    fun getTextInputLayoutDifficulty() : TextInputLayout {
        return textInputLayoutDifficulty
    }

    interface CreateSubtaskViewListener {
        fun onRemoveClicked(subtaskPosition: Int)
    }
}