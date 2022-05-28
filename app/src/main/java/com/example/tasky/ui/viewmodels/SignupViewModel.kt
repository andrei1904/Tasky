package com.example.tasky.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.Account
import com.example.tasky.ui.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    val values = Companion

    private val account = Account()

    private val completedFields: MutableSet<String> = mutableSetOf()

    fun onSignup() : Single<Boolean> {
        return loginRepository.signUp(account)
    }

    fun setLoginField(value: String, type: String) {
        when (type) {
            EMAIL_VALUE -> {
                account.email = value
            }
            PASSWORD_VALUE -> {
                account.password = value
            }
        }

        if (value.isEmpty()) {
            completedFields.remove(type)
        } else {
            completedFields.add(type)
        }
    }

    fun isAccountValid(): Boolean {
        if (completedFields.size == NUMBER_OF_FIELDS) {
            return true
        }
        return false
    }

    fun getCompletedFields(): MutableSet<String> {
        return completedFields
    }

    companion object {
        const val EMAIL_VALUE = "email"
        const val PASSWORD_VALUE = "password"
        val ALL_FIELDS = listOf(EMAIL_VALUE, PASSWORD_VALUE)
        private const val NUMBER_OF_FIELDS = 2
    }
}