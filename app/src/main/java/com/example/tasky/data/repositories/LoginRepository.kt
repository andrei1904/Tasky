package com.example.tasky.data.repositories

import com.example.tasky.data.model.entities.Account
import com.example.tasky.data.model.entities.UserWIthTasks
import com.example.tasky.data.remote.TaskyApi
import com.example.tasky.utils.preferences.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val taskyApi: TaskyApi,
    private val preferenceHelper: PreferenceHelper
) {

    fun signIn(account: Account): Single<Boolean> {

        return Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(
                account.username.trim(),
                account.password.trim()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(task.exception ?: Exception("Failed to login"))
                    }
                }
        }
    }

    fun signUp(account: Account): Single<Boolean> {

        return Single.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(
                account.username.trim(),
                account.password.trim()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(task.exception ?: Exception("Failed to create account"))
                    }
                }
        }
    }

    fun isUserLoggedIn(): Single<Boolean> {

        return Single.create { emitter ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                emitter.onSuccess(true)
            } else {
                emitter.onSuccess(false);
            }
        }

    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun isLoggedIn(): Boolean {
        firebaseAuth.currentUser?.run {
            return true
        } ?: kotlin.run {
            return false
        }
    }

    fun createUser(account: Account): Single<Boolean> {
        return Single.create { emitter ->
            val response = taskyApi.createUser(account).execute().body()

            if (response == null) {
                emitter.onError(Exception("Failed to create user"))
            } else {
                emitter.onSuccess(true)
            }
        }
    }

    fun login(account: Account): Single<Boolean> {
        return Single.create { emitter ->
            preferenceHelper.setToken("")
            val response = taskyApi.login(account).execute().body()

            if (response == null) {
                emitter.onError(Exception("Failed to login"))
            } else {
                preferenceHelper.setToken(response.accessToken)
                preferenceHelper.setUserId(response.user.id)
                emitter.onSuccess(true)
            }
        }
    }

    fun logout(): Single<Boolean> {
        return Single.create { emitter ->
            val response = taskyApi.logout().execute().body()

            if (response == null) {
                emitter.onError(Exception("Failed to login"))
            } else {
                if (response) {
                    emitter.onSuccess(true)
                } else {
                    emitter.onError(Exception("Failed"))
                }
            }
        }
    }
}