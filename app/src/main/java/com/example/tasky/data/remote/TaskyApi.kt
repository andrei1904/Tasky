package com.example.tasky.data.remote

import com.example.tasky.data.model.entities.Account
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.UserDetails
import com.example.tasky.data.model.entities.UserWIthTasks
import com.example.tasky.data.model.responses.CreateUserResponse
import com.example.tasky.data.model.responses.LoginResponse
import com.example.tasky.di.NetworkModule
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.*

interface TaskyApi {

    @POST(NetworkModule.CREATE_USER)
    fun createUser(@Body account: Account): Call<CreateUserResponse>

    @POST(NetworkModule.LOGIN)

    fun login(@Body account: Account): Call<LoginResponse>

    @PUT(NetworkModule.UPDATE_USER)
    fun updateUser(@Path("id") id: Long, @Body userDetails: UserDetails): Call<UserDetails>

    @GET(NetworkModule.GET_USER)
    fun getUser(@Path("id") id: Long): Call<UserDetails>

    @POST(NetworkModule.LOGOUT)
    fun logout(): Call<Boolean>

    @GET(NetworkModule.GET_USER_TASKS)
    fun getUserWithTasks(@Path("id") id: Long): Call<UserWIthTasks>

    @POST(NetworkModule.ADD_TASK)
    fun addTask(@Path("id") id: Long, @Body task: Task)
}