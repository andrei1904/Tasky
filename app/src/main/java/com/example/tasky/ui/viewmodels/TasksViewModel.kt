package com.example.tasky.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.model.enums.SubtaskStatus
import com.example.tasky.data.model.requests.ProgressStatusRequest
import com.example.tasky.data.repositories.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    fun getAllTasks(): Single<List<TaskWithSubtasks>> {
        return tasksRepository.getAllTasks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteTaskById(id: Long): Single<Boolean> {
        return tasksRepository.deleteTaskById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateProgress(taskId: Long, progress: Int): Single<Boolean> {
        return tasksRepository.updateProgressStatus(taskId, progress)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateTimeSpent(taskId: Long, spentTime: Long): Single<Boolean> {
        return tasksRepository.updateTimeSpent(taskId, spentTime)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateSubtask(subtask: Subtask): Single<Subtask> {
        return tasksRepository.updateSubtask(subtask)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}