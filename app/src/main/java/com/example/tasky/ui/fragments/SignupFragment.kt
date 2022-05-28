package com.example.tasky.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tasky.databinding.FragmentSigupBinding
import com.example.tasky.ui.activites.MainActivity
import com.example.tasky.ui.viewmodels.LoginViewModel
import com.example.tasky.ui.viewmodels.SignupViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSigupBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<SignupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addValidationListeners()

        binding.buttonCreateAccount.setOnClickListener {
            createAccountClicked()
        }
    }

    private fun createAccountClicked() {
        if (!viewModel.isAccountValid()) {
            validateOnCreateClicked()
            return
        }

        viewModel.onSignup()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result == true) {
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                    }
                },
                { throwable ->
                    Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                }
            )
    }

    private fun validateOnCreateClicked() {
        var isValid: Boolean
        for (field in viewModel.values.ALL_FIELDS) {
            isValid = viewModel.getCompletedFields().contains(field)

            when (field) {
                LoginViewModel.EMAIL_VALUE -> {
                    setValidationError(binding.textInputLayoutEmail, isValid)
                }
                LoginViewModel.PASSWORD_VALUE -> {
                    setValidationError(binding.textInputLayoutPassword, isValid)
                }
            }
        }
    }
    private fun addValidationListeners() {
        addValidationListener(binding.textInputLayoutEmail, viewModel.values.EMAIL_VALUE)
        addValidationListener(binding.textInputLayoutPassword, viewModel.values.PASSWORD_VALUE)
    }

    private fun setValidationError(textInputLayout: TextInputLayout, isValid: Boolean) {
        if (isValid) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.error = "Add " + textInputLayout.hint
        }
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
            viewModel.setLoginField(value, field)
        }
    }
}