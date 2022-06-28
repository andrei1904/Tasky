package com.example.tasky.data.remote

import com.example.tasky.data.model.entities.*
import com.example.tasky.data.model.requests.RefreshToken
import com.example.tasky.data.model.responses.CreateUserResponse
import com.example.tasky.data.model.responses.LoginResponse
import com.example.tasky.di.NetworkModule
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

    @PUT(NetworkModule.REFRESH)
    fun refreshToken(@Body refreshToken: RefreshToken): Call<RefreshToken>

    @POST(NetworkModule.ADD_TASK)
    fun addTask(@Body task: Task): Call<Long>

    @GET(NetworkModule.GET_TASKS)
    fun getAllTasks(@Path("status") status: String): Call<List<TaskWithSubtasks>>

    @DELETE(NetworkModule.DELETE_TASK)
    fun deleteTask(@Path("taskId") taskId: Long): Call<Boolean>

    @POST(NetworkModule.ADD_SUBTASKS)
    fun addSubtasks(@Path("taskId") taskId: Long, @Body subtasks: List<Subtask>): Call<List<Long>>

    @PUT(NetworkModule.UPDATE_TASK_PROGRESS)
    fun updateTaskProgressStatus(@Path("taskId") taskId: Long, @Body progress: Int): Call<Boolean>

    @PUT(NetworkModule.UPDATE_TASK_TIME)
    fun updateTaskSpentTime(@Path("taskId") taskId: Long, @Body spentTime: Long): Call<Boolean>

    @PUT(NetworkModule.UPDATE_SUBTASK)
    fun updateSubtask(@Path("subtaskId") subtaskId: Long, @Body subtask: Subtask): Call<Subtask>
}