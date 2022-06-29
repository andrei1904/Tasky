package com.example.tasky

import com.example.tasky.data.model.entities.Account
import com.example.tasky.data.remote.TaskyApi
import com.example.tasky.data.repositories.LoginRepository
import com.example.tasky.data.repositories.UserRepository
import com.example.tasky.di.NetworkModule
import com.example.tasky.utils.interceptor.AuthInterceptor
import com.example.tasky.utils.preferences.PreferenceHelper
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import org.mockito.Mock
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.io.File

class UserTest {

    @Mock
    lateinit var userRepo: UserRepository

    @Mock
    lateinit var taskyApi: TaskyApi

    @Mock
    lateinit var preferenceHelper: PreferenceHelper

    @Mock
    lateinit var loginRepository: LoginRepository

    @Test
    fun addUser() {
        loginRepository = LoginRepository(taskyApi, preferenceHelper)
        userRepo = UserRepository(taskyApi, preferenceHelper)

        loginRepository.login(Account("a", "a"))
        userRepo.getUserDetails()
    }
}