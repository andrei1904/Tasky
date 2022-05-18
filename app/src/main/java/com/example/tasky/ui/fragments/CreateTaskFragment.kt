package com.example.tasky.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.tasky.R
import com.example.tasky.data.model.Icon
import com.example.tasky.data.model.IconType
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.databinding.FragmentCreateTaskBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.CreateTaskViewModel
import com.example.tasky.utils.CalendarManager
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTaskFragment : BaseFragment<FragmentCreateTaskBinding>() {

    private val viewModel by activityViewModels<CreateTaskViewModel>()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentCreateTaskBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
        } else {
            isFirstLoaded = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstLoaded) {
            initPriorityDropdownMenu()
            initDateTimePicker()

            addValidationListeners()
        }
    }

    override fun getRightIcons(): List<Icon> {
        return listOf(Icon(
            IconType.NEXT_ICON,
            {
                nextButtonClicked()
            }
        ))
    }

    override fun getLeftIcon(): Icon {
        return Icon(
            IconType.BACK_ICON,
            {
                (activity as BaseActivity).getFragmentNavigation().popBackStack()
            }
        )
    }

    override fun getTitle(): String {
        return getString(R.string.create_task)
    }

    private fun nextButtonClicked() {
//        if (!viewModel.isTaskValid()) {
//            validateOnNextCliked()
//            return
//        }

        (activity as BaseActivity).getFragmentNavigation().replaceFragment(CreateSubtaskFragment())
    }

    private fun validateOnNextCliked() {
        var isValid: Boolean
        for (field in viewModel.values.ALL_TASK_FIELDS) {
            isValid = viewModel.getTaskCompltedFields().contains(field)

            when (field) {
                viewModel.values.DOMAIN_VALUE -> {
                    setValidationError(binding.textInputDomain, isValid)
                }
                viewModel.values.TITLE_VALUE -> {
                    setValidationError(binding.textInputTitle, isValid)
                }
                viewModel.values.PRIORITY_VALUE -> {
                    setValidationError(binding.textInputPriority, isValid)
                }
                viewModel.values.DEADLINE_VALUE -> {
                    setValidationError(binding.textInputDeadline, isValid)

                }
                viewModel.values.DESCRIPTION_VALUE -> {
                    setValidationError(binding.textInputDescription, isValid)
                }
            }
        }
    }

    private fun setValidationError(textInputLayout: TextInputLayout, isValid: Boolean) {
        if (isValid) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.error = "Add " + textInputLayout.hint
        }
    }

    private fun addValidationListeners() {
        addValidationListener(binding.textInputDomain, viewModel.values.DOMAIN_VALUE)
        addValidationListener(binding.textInputTitle, viewModel.values.TITLE_VALUE)
        addValidationListener(binding.textInputPriority, viewModel.values.PRIORITY_VALUE)
        addValidationListener(binding.textInputDeadline, viewModel.values.DEADLINE_VALUE)
        addValidationListener(binding.textInputDescription, viewModel.values.DESCRIPTION_VALUE)
    }

    private fun addValidationListener(textInputLayout: TextInputLayout, field: String) {
        val editText = textInputLayout.editText ?: return

        editText.addTextChangedListener { editable ->
            val value = editable.toString()

            if (value.isEmpty()) {
                textInputLayout.error = "Add " + textInputLayout.hint
            } else {
                textInputLayout.isErrorEnabled = false
            }
            viewModel.setTaskField(value, field)
        }
    }

    private fun initPriorityDropdownMenu() {
        (binding.textInputPriority.editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_priority_list,
                listOf(Priority.LOW.value, Priority.MEDIUM.value, Priority.HIGH.value)
            )
        )
    }

    private fun initDateTimePicker() {
        val editText = binding.textInputDeadline.editText ?: return

        editText.inputType = InputType.TYPE_NULL
        editText.keyListener = null

        editText.setOnClickListener {
            CalendarManager().openDatePickerDialog(editText, requireContext())
        }
    }
}