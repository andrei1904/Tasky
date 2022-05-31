package com.example.tasky.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.entities.Resource
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.repositories.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val createTaskResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    private val allTasks: MutableLiveData<Resource<List<Task>>> = MutableLiveData()
    private val deleteTaskResult: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    private val disposables: CompositeDisposable = CompositeDisposable()

    private val allTasksWithSubtasks: MutableLiveData<Resource<List<TaskWithSubtasks>>> = MutableLiveData()

    fun getAllTasks(): MutableLiveData<Resource<List<Task>>> {
        disposables.add(
            tasksRepository.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        allTasks.postValue(Resource.success(result))
                    },
                    {
                        allTasks.postValue(Resource.error(it.message.toString(), null))
                    }
                )
        )
        return allTasks
    }

    fun getAll(): MutableLiveData<Resource<List<TaskWithSubtasks>>> {
        disposables.add(
            tasksRepository.getAllTasksWithSubtasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        allTasksWithSubtasks.postValue(Resource.success(result))
                    },
                    {
                        allTasksWithSubtasks.postValue(Resource.error(it.message.toString(), null))
                    }
                )
        )
        return allTasksWithSubtasks
    }

    fun deleteTaskId(id: Long): Single<Int> {
         return tasksRepository.deleteTaskById(id)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}