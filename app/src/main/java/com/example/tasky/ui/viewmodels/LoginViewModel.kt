package com.example.tasky.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.data.model.Resource
import com.example.tasky.ui.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    private val _userLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)

    fun onLogin(email: String, password: String) : Single<Boolean> {
        return loginRepository.signIn(email, password);
    }

    fun isUserLoggedIn(): MutableLiveData<Boolean> {
        viewModelScope.launch {
            _userLoggedIn.value = loginRepository.isLoggedIn()
        }

        return _userLoggedIn;
    }

    fun onSignOut() {
        viewModelScope.launch {
            loginRepository.signOut()
        }
    }
}