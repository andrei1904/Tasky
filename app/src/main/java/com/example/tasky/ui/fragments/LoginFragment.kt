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
import com.example.tasky.databinding.FragmentLoginBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.activites.MainActivity
import com.example.tasky.ui.viewmodels.LoginViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addValidationListeners()

        binding.buttonSingIn.setOnClickListener {
            loginButtonClicked()
        }

        binding.buttonSingUp.setOnClickListener {
            (activity as BaseActivity).getFragmentNavigation().replaceFragment(SignupFragment())
        }
    }

    private fun loginButtonClicked() {
        if (!viewModel.isLoginValid()) {
            validateOnLoginClicked()
            return
        }

        viewModel.login()
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

    private fun validateOnLoginClicked() {
        var isValid: Boolean
        for (field in viewModel.values.ALL_FIELDS) {
            isValid = viewModel.getLoginCompletedFields().contains(field)

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

    private fun setValidationError(textInputLayout: TextInputLayout, isValid: Boolean) {
        if (isValid) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.error = "Add " + textInputLayout.hint
        }
    }

    private fun addValidationListeners() {
        addValidationListener(binding.textInputLayoutEmail, viewModel.values.EMAIL_VALUE)
        addValidationListener(binding.textInputLayoutPassword, viewModel.values.PASSWORD_VALUE)
    }

    private fun addValidationListener(textInputLayout: TextInputLayout, field: String) {
        val editText = textInputLayout.editText ?: return

        editText.addTextChangedListener { editable ->
            val value = editable.toString()
            viewModel.setLoginField(value, field)
        }
    }

    fun onBackPressed() {
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}