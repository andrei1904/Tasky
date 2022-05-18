package com.example.tasky.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.Subtask
import com.example.tasky.data.model.Task
import com.example.tasky.data.model.TaskWithSubtasks
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.ui.repositories.TasksRepository
import com.example.tasky.utils.CalendarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    companion object {
        const val DOMAIN_VALUE = "domain"
        const val TITLE_VALUE = "title"
        const val DEADLINE_VALUE = "deadline"
        const val PRIORITY_VALUE = "priority"
        const val DESCRIPTION_VALUE = "description"
        val ALL_TASK_FIELDS =
            listOf(DOMAIN_VALUE, TITLE_VALUE, DEADLINE_VALUE, PRIORITY_VALUE, DESCRIPTION_VALUE)
        val ALL_SUBTASK_FIELDS = listOf(TITLE_VALUE, DESCRIPTION_VALUE)
        private const val NUMBER_OF_FIELDS_FOR_TASK = 5
        private const val NUMBER_OF_FIELDS_FOR_SUBTASK = 2
        const val DATE_TIME_FORMAT = "dd MMM yyyy hh:mm"
    }

    val values = Companion

    private val completedFieldsForTask: MutableSet<String> = mutableSetOf()

    private val completedFieldsForSubtasks: ArrayList<MutableSet<String>> = arrayListOf()

    private val task: Task = Task()

    private val subtasks: ArrayList<Subtask> = arrayListOf()

    fun setTaskField(value: String, type: String) {
        when (type) {
            DOMAIN_VALUE -> {
                task.domain = value
            }
            TITLE_VALUE -> {
                task.title = value
            }
            PRIORITY_VALUE -> {
                task.priority = Priority.valueOf(value.uppercase(Locale.getDefault()))
            }
            DEADLINE_VALUE -> {
                val date: Date? =
                    SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).parse(value)
                if (date != null) {
                    task.deadline = date.time
                }
            }
            DESCRIPTION_VALUE -> {
                task.description = value
            }
        }

        if (value.isEmpty()) {
            completedFieldsForTask.remove(type)
        } else {
            completedFieldsForTask.add(type)
        }
    }

    fun isTaskValid(): Boolean {
        if (completedFieldsForTask.size == NUMBER_OF_FIELDS_FOR_TASK) {
            return true
        }
        return false
    }

    fun getTaskCompltedFields(): MutableSet<String> {
        return completedFieldsForTask
    }

    fun addTask(): Single<Boolean> {
        return tasksRepository.createTask(task)
            .map { id ->
                id > 0
            }
    }

    fun addTaskSubtasks(): Single<Boolean> {
        return Single.create { emitter ->
            tasksRepository.createTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ id ->
                    if (id > 0) {
                        for (subtask in subtasks) {
                            subtask.mainTaskId = id
                        }
                        tasksRepository.createSubtasks(subtasks)
                        emitter.onSuccess(true)
                    }
                }, { throwable ->
                    emitter.onError(throwable)
                })
        }
    }

    fun addSubtask() {
        subtasks.add(Subtask())
        completedFieldsForSubtasks.add(mutableSetOf())
    }

    fun setSubtaskField(value: String, type: String, subtaskNumber: Int) {
        val subtask = subtasks[subtaskNumber]
        when (type) {
            TITLE_VALUE -> {
                subtask.title = value
            }
            DESCRIPTION_VALUE -> {
                subtask.description = value
            }
        }

        if (value.isEmpty()) {
            completedFieldsForSubtasks[subtaskNumber].remove(type)
        } else {
            completedFieldsForSubtasks[subtaskNumber].add(type)
        }
    }

    fun removeSubtask(subtaskNumber: Int) {
        subtasks.removeAt(subtaskNumber)
        completedFieldsForSubtasks.removeAt(subtaskNumber)
    }

    fun areSubtasksValid(): Boolean {
        for (completedFieldsForSubtask in completedFieldsForSubtasks) {
            if (completedFieldsForSubtask.size != NUMBER_OF_FIELDS_FOR_SUBTASK) {
                return false
            }
        }
        return true
    }

    fun getSubtaskCompletedFields(): ArrayList<MutableSet<String>> {
        return completedFieldsForSubtasks
    }

    fun setImposedDeadlineField(value: String) {
        val date: Date? =
            SimpleDateFormat(CalendarManager.DATE_TIME_FORMAT, Locale.getDefault()).parse(value)
        if (date != null) {
            task.imposedDeadline = date.time
        }
    }
}