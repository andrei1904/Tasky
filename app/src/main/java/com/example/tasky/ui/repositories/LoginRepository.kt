package com.example.tasky.ui.repositories

import com.example.tasky.data.model.Account
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun signIn(account: Account): Single<Boolean> {

        return Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(account.email.trim(), account.password.trim())
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
            firebaseAuth.createUserWithEmailAndPassword(account.email.trim(), account.password.trim())
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
}