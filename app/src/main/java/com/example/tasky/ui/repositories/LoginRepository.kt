package com.example.tasky.ui.repositories

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun signIn(email: String, password: String): Single<Boolean> {

        return Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(task.exception ?: Exception("Failed to login"))
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