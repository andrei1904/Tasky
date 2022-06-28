package com.example.tasky.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.databinding.FragmentCreateTaskSelfDeadlineBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.CreateTaskViewModel
import com.example.tasky.utils.CalendarManager
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CreateTaskSelfDeadlineFragment : BaseFragment<FragmentCreateTaskSelfDeadlineBinding>() {

    private val viewModel by activityViewModels<CreateTaskViewModel>()

    private val rightIconList: ArrayList<Icon> = ArrayList()

    private val disposable = CompositeDisposable()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentCreateTaskSelfDeadlineBinding.inflate(inflater, container, false)

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
            initDateTimePicker()
            addValidationListener(binding.textInputDeadline)
        }
    }

    override fun getRightIcons(): List<Icon?> {
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

    override fun getTitle() = "Add self-imposed deadline"

    private fun onCheckClicked() {
        if (binding.textInputDeadline.editText?.text?.isEmpty() == true) {
            binding.textInputDeadline.error = "Add " + binding.textInputDeadline.hint
            return
        }
        addTaskSubtasks()
    }

    private fun initButtons() {

        initOnYesClicked()
        initOnNoClicked()
    }

    private fun initOnYesClicked() {
        binding.buttonYes.setOnClickListener {
            hideMessage()
            addRightIcons()
            showForm()
        }
    }

    private fun hideMessage() {
        binding.textViewQuestion.visibility = View.GONE
        binding.buttonYes.visibility = View.GONE
        binding.buttonNo.visibility = View.GONE
    }

    private fun addRightIcons() {
        rightIconList.add(Icon(IconType.CHECK_ICON, { onCheckClicked() }))
        rightIconsChanged()
    }

    private fun showForm() {
        binding.textInputDeadline.visibility = View.VISIBLE
    }

    private fun initOnNoClicked() {
        binding.buttonNo.setOnClickListener {
            context?.let { AlertDialog.Builder(it) }
                ?.setTitle("Add task")
                ?.setMessage("Are you sure to you want to add this task without self deadline?")
                ?.setPositiveButton("Yes") { _, _ ->
                    addTaskSubtasks()
                }
                ?.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                ?.show()
        }
    }

    private fun initDateTimePicker() {
        val editText = binding.textInputDeadline.editText ?: return

        editText.inputType = InputType.TYPE_NULL
        editText.keyListener = null
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.callOnClick()
            }
        }
        editText.setOnClickListener {
            CalendarManager().openDatePickerDialog(editText, requireContext())
        }

    }

    private fun addTaskSubtasks() {
        disposable.add(
            viewModel.addTaskSubtasks()
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

    private fun addValidationListener(textInputLayout: TextInputLayout) {
        val editText = textInputLayout.editText ?: return

        editText.addTextChangedListener { editable ->
            val value = editable.toString()

            if (value.isEmpty()) {
                textInputLayout.error = "Add " + textInputLayout.hint
            } else {
                textInputLayout.isErrorEnabled = false
            }
            viewModel.setImposedDeadlineField(value)
        }
    }
}