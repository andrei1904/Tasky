package com.example.tasky.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.data.model.entities.Account
import com.example.tasky.data.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    val values = Companion

    private val _userLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)

    private val account = Account()

    private val completedFieldsForLogin: MutableSet<String> = mutableSetOf()

    fun setLoginField(value: String, type: String) {
        when (type) {
            EMAIL_VALUE -> {
                account.username = value
            }
            PASSWORD_VALUE -> {
                account.password = value
            }
        }

        if (value.isEmpty()) {
            completedFieldsForLogin.remove(type)
        } else {
            completedFieldsForLogin.add(type)
        }
    }

    fun isLoginValid(): Boolean {
        if (completedFieldsForLogin.size == NUMBER_OF_FIELDS) {
            return true
        }
        return false
    }

    fun getLoginCompletedFields() : MutableSet<String> {
        return completedFieldsForLogin
    }

//    fun onLogin() : Single<Boolean> {
//        return loginRepository.signIn(account);
//    }

    fun login(): Single<Boolean> {
        return loginRepository.login(account)
    }

    fun isUserLoggedIn(): MutableLiveData<Boolean> {
        viewModelScope.launch {
            _userLoggedIn.value = false
        }

        return _userLoggedIn;
    }

//    fun onSignOut() {
//        viewModelScope.launch {
//            loginRepository.signOut()
//        }
//    }

    companion object {
        const val EMAIL_VALUE = "email"
        const val PASSWORD_VALUE = "password"
        val ALL_FIELDS = listOf(EMAIL_VALUE, PASSWORD_VALUE)
        private const val NUMBER_OF_FIELDS = 2
    }
}