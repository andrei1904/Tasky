package com.example.tasky.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.tasky.R
import com.example.tasky.data.model.Icon
import com.example.tasky.data.model.IconType
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.databinding.FragmentCreateTaskBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.CreateTaskViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateTaskFragment : BaseFragment<FragmentCreateTaskBinding>() {

    private val viewModel by viewModels<CreateTaskViewModel>()

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

    override fun getRightIcon(): Icon {
        return Icon(
            IconType.NEXT_ICON,
            {
                nextButtonClicked()
            }
        )
    }

    override fun getLeftIcon(): Icon {
        return Icon(
            IconType.BACK_ICON,
            {
                (activity as BaseActivity).getFragmentNavigation().replaceFragment(TasksFragment())
            }
        )
    }

    override fun getTitle(): String {
        return getString(R.string.create_task)
    }

    private fun nextButtonClicked() {

        if (!viewModel.isTaskValid()) {
            validateOnNextCliked()
            return
        }

        (activity as BaseActivity).getFragmentNavigation().replaceFragment(CreateSubtaskFragment())
//        viewModel.createTask(task).observe(viewLifecycleOwner) { result ->
//            if (result.status == Status.SUCCESS) {
//                (activity as BaseActivity).getFragmentNavigation()
//                    .replaceFragment(TasksFragment())
//            } else {
//                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun validateOnNextCliked() {
        var isValid: Boolean
        for (field in viewModel.values.ALL_FIELDS) {
            isValid = viewModel.getCompltedFields().contains(field)

            when (field) {
                CreateTaskViewModel.DOMAIN_VALUE -> {
                    setValidationError(binding.textInputDomain, isValid)
                }
                CreateTaskViewModel.TITLE_VALUE -> {
                    setValidationError(binding.textInputTitle, isValid)
                }
                CreateTaskViewModel.PRIORITY_VALUE -> {
                    setValidationError(binding.textInputPriority, isValid)
                }
                CreateTaskViewModel.DEADLINE_VALUE -> {
                    setValidationError(binding.textInputDeadline, isValid)

                }
                CreateTaskViewModel.DESCRIPTION_VALUE -> {
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

    private fun addValidationListener(textInputLayout: TextInputLayout, taskField: String) {
        val editText = textInputLayout.editText ?: return

        editText.addTextChangedListener { editable ->
            val value = editable.toString()

            if (value.isEmpty()) {
                textInputLayout.error = "Add " + textInputLayout.hint
            } else {
                textInputLayout.isErrorEnabled = false
            }
            viewModel.setTaskField(value, taskField)
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
            openDatePickerDialog(editText)
        }

    }

    private fun openDatePickerDialog(editText: EditText) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->

                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day, hour, minute)
                        calendar.timeInMillis

                        editText.setText(
                            getDateTimeFromMillis(
                                calendar.timeInMillis,
                                SimpleDateFormat(
                                    viewModel.values.DATE_TIME_FORMAT,
                                    Locale.ENGLISH
                                )
                            )
                        )
                    },
                    startHour,
                    startMinute,
                    true
                ).show()
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }

    private fun getDateTimeFromMillis(millis: Long, formatter: SimpleDateFormat): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        return formatter.format(calendar.time)
    }
}