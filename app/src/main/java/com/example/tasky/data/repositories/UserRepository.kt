package com.example.tasky.data.repositories

import android.content.SharedPreferences
import com.example.tasky.data.model.entities.UserDetails
import com.example.tasky.data.remote.TaskyApi
import com.example.tasky.utils.preferences.PreferenceHelper
import io.reactivex.rxjava3.core.Single
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val taskyApi: TaskyApi,
    private val preferenceHelper: PreferenceHelper
) {

    fun updateUser(userDetails: UserDetails): Single<Boolean> {
        return Single.create { emitter ->
            val userId = preferenceHelper.getUserId()
            val response = taskyApi.updateUser(userId, userDetails).execute()

            if (!response.isSuccessful) {
                emitter.onError(Exception())
            } else {

                val data = response.body()
                if (data != null) {
                    emitter.onSuccess(true)
                }
            }
        }
    }

    fun getUserDetails(): Single<UserDetails> {
        return Single.create { emitter ->
            val response = taskyApi.getUser(preferenceHelper.getUserId()).execute()

            if (!response.isSuccessful) {
                emitter.onError(Exception())
            } else {

                val data = response.body()
                if (data != null) {
                    emitter.onSuccess(data)
                }
            }
        }
    }
}