package com.example.tasky.di

import android.app.Application
import com.example.tasky.data.remote.TaskyApi
import com.example.tasky.utils.interceptor.AuthInterceptor
import com.example.tasky.utils.preferences.PreferenceHelper
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(moshiConverterFactory: MoshiConverterFactory, okHttpClient: OkHttpClient):
            Retrofit {

        return Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideTaskyApi(retrofit: Retrofit): TaskyApi {
        return retrofit.create(TaskyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        val moshi = Moshi.Builder().build()

        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(app: Application, preferenceHelper: PreferenceHelper): OkHttpClient {
        val cacheDir = File(app.cacheDir, "http")
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(preferenceHelper))
            .cache(Cache(cacheDir, DISK_CACHE_SIZE))
            .build()
    }

    companion object {
        private const val DISK_CACHE_SIZE = (50 * 1024 * 1024).toLong()
//        const val API_URL = "http://10.0.2.2:5286"
        const val API_URL = "https://taskyserver.azurewebsites.net"
        const val CREATE_USER = "/api/users"
        const val UPDATE_USER = "/api/users/{id}"
        const val LOGIN = "/api/account/login"
        const val GET_USER = "/api/users/{id}"
        const val LOGOUT = "/api/account/logout"
        const val REFRESH = "/api/account/refresh-token"
        const val ADD_TASK = "/api/tasks"
        const val GET_TASKS = "/api/tasks/{status}"
        const val DELETE_TASK = "/api/tasks/{taskId}"
        const val ADD_SUBTASKS = "/api/subtasks/{taskId}"
        const val UPDATE_TASK_PROGRESS = "/api/tasks/{taskId}/progress"
        const val UPDATE_TASK_TIME = "/api/tasks/{taskId}/time"
        const val UPDATE_SUBTASK = "/api/subtasks/{subtaskId}"
    }
}