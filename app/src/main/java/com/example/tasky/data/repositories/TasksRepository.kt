package com.example.tasky.data.repositories

import com.example.tasky.data.local.AppDatabase
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.remote.TaskyApi
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val taskyApi: TaskyApi
) {
    fun deleteTaskById(id: Long): Single<Boolean> {
        return Single.create { emitter ->

            val result = taskyApi.deleteTask(id).execute()

            if (result.isSuccessful) {
                val data = result.body()

                if (data != null) {
                    appDatabase.TaskDao().deleteTaskById(id)
                    emitter.onSuccess(data)
                }
                if (data == null) {
                    emitter.onError(Exception("Error"))
                }
            } else {
                emitter.onError(Exception("Error"))
            }
        }
    }

    fun getAllTasks(status: String): Single<List<TaskWithSubtasks>> {
        return Single.create { emitter ->

            val result = taskyApi.getAllTasks(status).execute()

            if (result.isSuccessful) {
                val tasksWithSubtasks = result.body()

                if (tasksWithSubtasks != null) {

                    for (taskWithSubtasks in tasksWithSubtasks) {
                        appDatabase.TaskDao().insertTask(taskWithSubtasks.task)
                    }
                    emitter.onSuccess(tasksWithSubtasks)
                }
            } else {
                emitter.onError(Exception("Error"))
            }
        }
    }

    fun addTask(task: Task): Single<Long> {
        return Single.create { emitter ->

            val result = taskyApi.addTask(task).execute()

            if (result.isSuccessful) {
                val taskId = result.body()

                if (taskId != null) {
                    task.taskId = taskId
                    appDatabase.TaskDao().insertTask(task)

                    emitter.onSuccess(taskId)
                } else {
                    emitter.onError(Exception("Error"))
                }
            } else {
                emitter.onError(Exception("Error"))
            }
        }
    }

    fun addSubtasks(taskId: Long, subtasks: ArrayList<Subtask>): Single<Long> {
        return Single.create { emitter ->

            val result = taskyApi.addSubtasks(taskId, subtasks).execute()

            if (result.isSuccessful) {
                val data = result.body()

                if (data != null) {

                    for (index in data.indices) {
                        subtasks[index].subtaskId = data[index]
                    }
                    appDatabase.TaskDao().insertSubtasks(subtasks)

                    emitter.onSuccess(taskId)
                } else {
                    emitter.onError(Exception("Error"))
                }
            } else {
                emitter.onError(Exception("Error"))
            }
        }
    }

    fun updateProgressStatus(taskId: Long, progress: Int): Single<Boolean> {
        return Single.create { emitter ->

            val result = taskyApi.updateTaskProgressStatus(taskId, progress).execute()

            if (result.isSuccessful) {
                val data = result.body()

                if (data != null) {
                    emitter.onSuccess(data)
                } else {
                    emitter.onError(Exception(result.message()))
                }
            } else {
                emitter.onError(Exception(result.message()))
            }
        }
    }

    fun updateTimeSpent(taskId: Long, spentTime: Long): Single<Boolean> {
        return Single.create { emitter ->

            val result = taskyApi.updateTaskSpentTime(taskId, spentTime).execute()

            if (result.isSuccessful) {
                val data = result.body()

                if (data != null) {
                    emitter.onSuccess(data)
                } else {
                    emitter.onError(Exception(result.message()))
                }
            } else {
                emitter.onError(Exception(result.message()))
            }
        }
    }

    fun updateSubtask(subtask: Subtask): Single<Subtask> {
        return Single.create { emitter ->

            val result = taskyApi.updateSubtask(subtask.subtaskId, subtask).execute()

            if (result.isSuccessful) {
                val data = result.body()

                if (data != null) {
                    emitter.onSuccess(data)
                } else {
                    emitter.onError(Exception(result.message()))
                }
            } else {
                emitter.onError(Exception(result.message()))
            }
        }
    }
}