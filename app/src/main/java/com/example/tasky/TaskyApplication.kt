package com.example.tasky

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.exceptions.UndeliverableException
import java.io.IOException
import io.reactivex.rxjava3.plugins.RxJavaPlugins

@HiltAndroidApp
class TaskyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                val er = e.cause
            }
            if (e is IOException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                // that's likely a bug in the application
                val exceptionHandler =
                    Thread.currentThread().uncaughtExceptionHandler ?: return@setErrorHandler
                exceptionHandler.uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                val exceptionHandler =
                    Thread.currentThread().uncaughtExceptionHandler ?: return@setErrorHandler
                exceptionHandler.uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            Log.w("Undeliverable exception received, not sure what to do", e)
        }
    }
}