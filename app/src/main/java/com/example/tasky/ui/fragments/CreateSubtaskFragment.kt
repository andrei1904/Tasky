package com.example.tasky.ui.fragments

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.tasky.R
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.databinding.FragmentCreateSubtasksBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.CreateTaskViewModel
import com.example.tasky.ui.views.CreateSubtaskView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class CreateSubtaskFragment : BaseFragment<FragmentCreateSubtasksBinding>(),
    CreateSubtaskView.CreateSubtaskViewListener {

    private val viewModel by activityViewModels<CreateTaskViewModel>()

    private val rightIconList: ArrayList<Icon> = ArrayList()

    private val subtaskViewList: ArrayList<CreateSubtaskView> = ArrayList()

    private val subtaskTextWatcher: MutableMap<String, ArrayList<TextWatcher>> = mutableMapOf()

    private var numberOfSubtasks = 0

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentCreateSubtasksBinding.inflate(inflater, container, false)

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
            initButtons()

            subtaskTextWatcher[viewModel.values.TITLE_VALUE] = arrayListOf()
            subtaskTextWatcher[viewModel.values.DESCRIPTION_VALUE] = arrayListOf()
            subtaskTextWatcher[viewModel.values.DIFFICULTY_VALUE] = arrayListOf()
        }
    }

    override fun getRightIcons(): List<Icon> {
        return rightIconList
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
        return "Split into subtasks"
    }

    private fun initButtons() {

        initOnYesClicked()
        initOnNoClicked()
    }

    private fun initOnYesClicked() {
        binding.buttonAddSubtask.setOnClickListener {
            hideMessage()
            addSubtaskView()
            addRightIcons()
        }
    }

    private fun hideMessage() {
        binding.textViewQuestion.visibility = View.GONE
        binding.buttonAddSubtask.visibility = View.GONE
        binding.buttonNoAddSubtask.visibility = View.GONE
    }

    private fun addRightIcons() {
        rightIconList.add(Icon(IconType.ADD_ICON, onAddClicked()))
        rightIconList.add(Icon(IconType.NEXT_ICON, { onNextClicked() }))
        rightIconsChanged()
    }

    private fun onAddClicked(): View.OnClickListener {
        return View.OnClickListener {
            addSubtaskView()
        }
    }

    private fun onNextClicked() {

        setFields()
        if (!viewModel.areSubtasksValid()) {
            validateOnNextClicked()
            return
        }

        (activity as BaseActivity).getFragmentNavigation()
            .replaceFragment(CreateTaskSelfDeadlineFragment())
    }

    private fun addSubtaskView() {
        for (subtaskView in subtaskViewList) {
            subtaskView.hideDetails()
        }

        val subtaskView = context?.let { context -> CreateSubtaskView(context) } ?: return
        subtaskView.setListener(this)

        binding.constraintLayout.addView(subtaskView)
        subtaskViewList.add(subtaskView)

        subtaskView.setSubtaskNumber(numberOfSubtasks)
        viewModel.addSubtask()

        initDropdownMenu(subtaskView)

        numberOfSubtasks += 1
    }

    private fun initDropdownMenu(subtaskView: CreateSubtaskView) {
        (subtaskView.getTextInputLayoutDifficulty().editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_difficulty_list,
                listOf(Difficulty.EASY.value, Difficulty.MEDIUM.value, Difficulty.HARD.value)
            )
        )
    }

    private fun initOnNoClicked() {
        binding.buttonNoAddSubtask.setOnClickListener {
            context?.let { AlertDialog.Builder(it) }
                ?.setTitle("Add task")
                ?.setMessage("Are you sure to you don't want to add any subtasks?")
                ?.setPositiveButton("Yes") { _, _ ->
                    addTask()
                }
                ?.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                ?.show()
        }
    }

    private fun addTask() {
        compositeDisposable.add(
            viewModel.addTask()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result == true) {
                        (activity as BaseActivity).getFragmentNavigation()
                            .replaceFragment(TasksFragment())
                    }
                }, { throwable ->
                    throwable.message?.let { safeThrowable ->
                        Log.e(
                            CreateSubtaskFragment::class.java.canonicalName,
                            safeThrowable
                        )
                    }
                })
        )
    }

    private fun setFields() {
        for (i in 0 until numberOfSubtasks) {
            if (subtaskViewList[i].getTextInputLayoutTitle().editText?.text.toString()
                    .isNotEmpty()
            ) {
                viewModel.setSubtaskField(
                    subtaskViewList[i].getTextInputLayoutTitle().editText?.text.toString(),
                    viewModel.values.TITLE_VALUE, i
                )
            }

            if (subtaskViewList[i].getTextInputLayoutDescription().editText?.text.toString()
                    .isNotEmpty()
            ) {
                viewModel.setSubtaskField(
                    subtaskViewList[i].getTextInputLayoutDescription().editText?.text.toString(),
                    viewModel.values.DESCRIPTION_VALUE, i
                )            }

            if (subtaskViewList[i].getTextInputLayoutDifficulty().editText?.text.toString()
                    .isNotEmpty()
            ) {
                viewModel.setSubtaskField(
                    subtaskViewList[i].getTextInputLayoutDifficulty().editText?.text.toString(),
                    viewModel.values.DIFFICULTY_VALUE, i
                )            }
        }
    }

    private fun validateOnNextClicked() {
        var isValid: Boolean

        for ((position, completedFieldsForSubtask) in viewModel.getSubtaskCompletedFields()
            .withIndex()) {
            for (field in viewModel.values.ALL_SUBTASK_FIELDS) {

                isValid = completedFieldsForSubtask.contains(field)
                when (field) {
                    viewModel.values.TITLE_VALUE -> {
                        setValidationError(
                            subtaskViewList[position].getTextInputLayoutTitle(),
                            isValid
                        )
                    }
                    viewModel.values.DESCRIPTION_VALUE -> {
                        setValidationError(
                            subtaskViewList[position].getTextInputLayoutDescription(),
                            isValid
                        )
                    }
                    viewModel.values.DIFFICULTY_VALUE -> {
                        setValidationError(
                            subtaskViewList[position].getTextInputLayoutDifficulty(),
                            isValid
                        )
                    }
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

    private fun updateSubtaskViewNumbers() {
        for ((position, subtaskView) in subtaskViewList.withIndex()) {
            subtaskView.setSubtaskNumber(position)
        }
        numberOfSubtasks -= 1
    }

    override fun onRemoveClicked(subtaskPosition: Int) {
        binding.constraintLayout.removeView(subtaskViewList[subtaskPosition])
        subtaskViewList.removeAt(subtaskPosition)
        viewModel.removeSubtask(subtaskPosition)

        updateSubtaskViewNumbers()
    }
}