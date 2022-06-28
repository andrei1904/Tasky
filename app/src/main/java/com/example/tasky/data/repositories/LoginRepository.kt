package com.example.tasky.data.repositories

import com.example.tasky.data.model.entities.Account
import com.example.tasky.data.model.requests.RefreshToken
import com.example.tasky.data.remote.TaskyApi
import com.example.tasky.utils.preferences.PreferenceHelper
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val taskyApi: TaskyApi,
    private val preferenceHelper: PreferenceHelper
) {

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
                preferenceHelper.setRefreshToken(response.refreshToken)
                emitter.onSuccess(true)
            }
        }
    }

    fun logout(): Single<Boolean> {
        return Single.create { emitter ->
            val response = taskyApi.logout().execute().body()

            if (response == null) {
                emitter.onError(Exception("Failed to logout"))
            } else {
                if (response) {
                    preferenceHelper.clearAll()
                    emitter.onSuccess(true)
                } else {
                    emitter.onError(Exception("Failed"))
                }
            }
        }
    }

    fun isLoggedIn(): Single<Boolean> {
        return Single.create { emitter ->
            if (preferenceHelper.getToken() == "") {
                emitter.onError(Exception("Not logged in"))
            } else {
                val response = taskyApi.refreshToken(
                    RefreshToken(preferenceHelper.getToken(), preferenceHelper.getRefreshToken())
                ).execute().body()

                if (response == null) {
                    emitter.onError(Exception("Failed"))
                } else {
                    preferenceHelper.setToken(response.accessToken)
                    preferenceHelper.setRefreshToken(response.refreshToken)
                    emitter.onSuccess(true)
                }
            }
        }
    }
}