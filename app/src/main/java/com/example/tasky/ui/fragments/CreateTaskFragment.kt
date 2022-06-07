package com.example.tasky.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.example.tasky.R
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.databinding.FragmentCreateTaskBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.CreateTaskViewModel
import com.example.tasky.utils.CalendarManager
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTaskFragment : ValidatorFragment<FragmentCreateTaskBinding>() {

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
            initDropdownMenu()
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
        if (!viewModel.isTaskValid()) {
            validate(viewModel.values.ALL_TASK_FIELDS, viewModel.getTaskCompletedFields())
            return
        }

        (activity as BaseActivity).getFragmentNavigation().replaceFragment(CreateSubtaskFragment())
    }

    private fun initDropdownMenu() {
        (binding.textInputPriority.editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_priority_list,
                listOf(Priority.LOW.value, Priority.MEDIUM.value, Priority.HIGH.value)
            )
        )
        (binding.textInputDifficulty.editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_difficulty_list,
                listOf(Difficulty.EASY.value, Difficulty.MEDIUM.value, Difficulty.HARD.value)
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

    override fun getTextInputsMap(): Map<String, TextInputLayout> {
        val map = HashMap<String, TextInputLayout>()

        map[viewModel.values.DOMAIN_VALUE] = binding.textInputDomain
        map[viewModel.values.TITLE_VALUE] = binding.textInputTitle
        map[viewModel.values.PRIORITY_VALUE] = binding.textInputPriority
        map[viewModel.values.DEADLINE_VALUE] = binding.textInputDeadline
        map[viewModel.values.DESCRIPTION_VALUE] = binding.textInputDescription
        map[viewModel.values.DESCRIPTION_VALUE] = binding.textInputDescription

        return map
    }

    private fun addValidationListeners() {
        addValidationListener(binding.textInputDomain, viewModel.values.DOMAIN_VALUE)
        { x: String, y: String -> viewModel.setTaskField(x, y) }
        addValidationListener(binding.textInputTitle, viewModel.values.TITLE_VALUE)
        { x: String, y: String -> viewModel.setTaskField(x, y) }
        addValidationListener(binding.textInputPriority, viewModel.values.PRIORITY_VALUE)
        { x: String, y: String -> viewModel.setTaskField(x, y) }
        addValidationListener(binding.textInputDeadline, viewModel.values.DEADLINE_VALUE)
        { x: String, y: String -> viewModel.setTaskField(x, y) }
        addValidationListener(binding.textInputDescription, viewModel.values.DESCRIPTION_VALUE)
        { x: String, y: String -> viewModel.setTaskField(x, y) }
        addValidationListener(binding.textInputDifficulty, viewModel.values.DIFFICULTY_VALUE)
        { x: String, y: String -> viewModel.setTaskField(x, y) }
    }
}