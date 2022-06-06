package com.example.tasky.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.entities.UserDetails
import com.example.tasky.data.repositories.LoginRepository
import com.example.tasky.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val values = Companion

    private val userDetails = UserDetails()

    private val completedFields: MutableSet<String> = mutableSetOf()

    fun setUserFields(value: String, type: String) {
        when (type) {
            FIRST_NAME -> {
                userDetails.firstName = value
            }
            LAST_NAME -> {
                userDetails.lastName = value
            }
        }

        if (value.isEmpty()) {
            completedFields.remove(type)
        } else {
            completedFields.add(type)
        }
    }

    fun isValid(): Boolean {
        if (completedFields.size == NUMBER_OF_FIELDS) {
            return true
        }
        return false
    }

    fun getCompletedFields(): MutableSet<String> {
        return completedFields
    }

    fun updateUser(): Single<Boolean> {
        return userRepository.updateUser(userDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUser(): Single<UserDetails> {
        return userRepository.getUserDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Single<Boolean> {
        return loginRepository.logout()
    }

    companion object {
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        val ALL_FIELDS = listOf(FIRST_NAME, LAST_NAME)
        private const val NUMBER_OF_FIELDS = 2
    }
}