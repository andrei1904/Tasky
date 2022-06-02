package com.example.tasky.data.repositories

import com.example.tasky.data.local.AppDatabase
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.model.entities.UserWIthTasks
import com.example.tasky.data.remote.TaskyApi
import com.example.tasky.utils.preferences.PreferenceHelper
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val taskyApi: TaskyApi,
    private val preferenceHelper: PreferenceHelper
) {
    fun getAllTasks(): Single<List<Task>> {
        return appDatabase.TaskDao().getAll()
    }

    fun getAllTasksWithSubtasks(): Single<List<TaskWithSubtasks>> {
        return appDatabase.TaskDao().getAllTasksWithSubtasks()
    }

    fun deleteTaskById(id: Long): Single<Int> {
        return appDatabase.TaskDao().deleteTaskById(id)
    }

    fun createTask(task: Task): Single<Long> {
//        return appDatabase.TaskDao().insertTask(task)
        return Single.create { emitter ->

            val result = taskyApi.addTask(preferenceHelper.getUserId(), task)
        }
    }

    fun createSubtasks(subtasks: ArrayList<Subtask>) {
        appDatabase.TaskDao().insertSubtasks(subtasks)
    }

    fun getUserWithTasks(): Single<UserWIthTasks> {
        return Single.create { emitter ->

            val result = taskyApi.getUserWithTasks(preferenceHelper.getUserId()).execute()

            if (result.isSuccessful) {
                val data = result.body()

                if (data != null) {
                    emitter.onSuccess(data)
                }
            } else {
                emitter.onError(Exception("Error"))
            }
        }
    }
}