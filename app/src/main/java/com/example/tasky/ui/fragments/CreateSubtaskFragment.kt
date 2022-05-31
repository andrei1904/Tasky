package com.example.tasky.ui.fragments

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
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
        return "Add subtask"
    }

    private fun initButtons() {

        initOnYesClciked()
        initOnNoClciked()
    }

    private fun initOnYesClciked() {
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
        rightIconList.add(Icon(IconType.ADD_ICON, onAddClciked()))
        rightIconList.add(Icon(IconType.NEXT_ICON, { onNextClicked() }))
        rightIconsChanged()
    }

    private fun onAddClciked(): View.OnClickListener {
        return View.OnClickListener {
            addSubtaskView()
        }
    }

    private fun onNextClicked() {

        if (!viewModel.areSubtasksValid()) {
            validateOnNextCliked()
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

        addValidationListeners(numberOfSubtasks)

        numberOfSubtasks += 1
    }

    private fun initOnNoClciked() {
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

    private fun addValidationListeners(subtaskPosition: Int) {
        addValidationListener(
            subtaskViewList[subtaskPosition].getTextInputLayoutTitle(),
            viewModel.values.TITLE_VALUE,
            subtaskPosition
        )
        addValidationListener(
            subtaskViewList[subtaskPosition].getTextInputLayoutDescription(),
            viewModel.values.DESCRIPTION_VALUE,
            subtaskPosition
        )
    }

    private fun addValidationListener(
        textInputLayout: TextInputLayout,
        field: String,
        subtaskNumber: Int
    ) {
        val editText = textInputLayout.editText ?: return

        subtaskTextWatcher[field]?.add(
            editText.addTextChangedListener { editable ->
                val value = editable.toString()

                if (value.isEmpty()) {
                    textInputLayout.error = "Add " + textInputLayout.hint
                } else {
                    textInputLayout.isErrorEnabled = false
                }
                viewModel.setSubtaskField(value, field, subtaskNumber)
            })
    }

    private fun removeValidationListeners(subtaskPosition: Int) {
        removeValidationListener(
            subtaskViewList[subtaskPosition].getTextInputLayoutTitle(),
            viewModel.values.TITLE_VALUE,
            subtaskPosition
        )
        removeValidationListener(
            subtaskViewList[subtaskPosition].getTextInputLayoutTitle(),
            viewModel.values.DESCRIPTION_VALUE,
            subtaskPosition
        )
    }

    private fun removeValidationListener(
        textInputLayout: TextInputLayout,
        field: String,
        subtaskNumber: Int
    ) {
        val editText = textInputLayout.editText ?: return

        val textWatcher = subtaskTextWatcher[field]?.get(subtaskNumber)
        editText.removeTextChangedListener(textWatcher)
    }

    private fun validateOnNextCliked() {
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
        removeValidationListeners(subtaskPosition)

        var position = subtaskPosition + 1
        while (position < numberOfSubtasks) {
            removeValidationListeners(position)
            addValidationListeners(position - 1)
            position += 1
        }

        binding.constraintLayout.removeView(subtaskViewList[subtaskPosition])
        subtaskViewList.removeAt(subtaskPosition)
        viewModel.removeSubtask(subtaskPosition)

        updateSubtaskViewNumbers()
    }
}