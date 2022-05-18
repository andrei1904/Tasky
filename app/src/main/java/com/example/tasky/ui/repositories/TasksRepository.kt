package com.example.tasky.ui.repositories

import com.example.tasky.data.local.AppDatabase
import com.example.tasky.data.model.Subtask
import com.example.tasky.data.model.Task
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {
    fun getAllTasks(): Single<List<Task>> {
        return appDatabase.TaskDao().getAll()
    }

    fun deleteTaskById(id: Long): Single<Int> {
        return appDatabase.TaskDao().deleteTaskById(id)
    }

    fun createTask(task: Task): Single<Long> {
        return appDatabase.TaskDao().insertTask(task)
    }

    fun createSubtasks(subtasks: ArrayList<Subtask>) {
        appDatabase.TaskDao().insertSubtasks(subtasks)
    }
}