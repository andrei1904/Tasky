package com.example.tasky.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.data.repositories.TasksRepository
import com.example.tasky.utils.CalendarManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
                task.priority = Priority.valueOf(value.uppercase(Locale.ENGLISH))
            }
            DEADLINE_VALUE -> {
                val date: Date? =
                    SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH).parse(value)
                if (date != null) {
                    task.deadline = date.time
                }
            }
            DESCRIPTION_VALUE -> {
                task.description = value
            }
            DIFFICULTY_VALUE -> {
                task.difficulty = Difficulty.valueOf(value.uppercase(Locale.ENGLISH))
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

    fun getTaskCompletedFields(): MutableSet<String> {
        return completedFieldsForTask
    }

    fun addTask(): Single<Boolean> {
        return Single.create { emitter ->
            tasksRepository.addTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ id ->
                    if (id > 0) {
                        emitter.onSuccess(true)
                    }
                }, { throwable ->
                    emitter.onError(throwable)
                })
        }
    }

    fun addTaskSubtasks(): Single<Boolean> {
        return Single.create { emitter ->
            tasksRepository.addTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ id ->
                    if (id > 0) {
                        if (subtasks.size > 0) {
                            for (subtask in subtasks) {
                                subtask.mainTaskId = id
                            }
                            tasksRepository.addSubtasks(id, subtasks)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe({
                                    emitter.onSuccess(true)
                                }, {
                                    throwable ->
                                    emitter.onError(Exception(throwable.message))
                                })
                        }
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
            DIFFICULTY_VALUE -> {
                subtask.difficulty = Difficulty.valueOf(value.uppercase(Locale.ENGLISH))
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
            SimpleDateFormat(CalendarManager.DATE_TIME_FORMAT, Locale.ENGLISH).parse(value)
        if (date != null) {
            task.imposedDeadline = date.time
        }
    }

    fun setCompletedField(i: Int, value: String) {
        completedFieldsForSubtasks[i].add(value)
    }

    companion object {
        const val DOMAIN_VALUE = "domain"
        const val TITLE_VALUE = "title"
        const val DEADLINE_VALUE = "deadline"
        const val PRIORITY_VALUE = "priority"
        const val DESCRIPTION_VALUE = "description"
        const val DIFFICULTY_VALUE = "difficulty"
        val ALL_TASK_FIELDS =
            listOf(DOMAIN_VALUE, TITLE_VALUE, DEADLINE_VALUE, PRIORITY_VALUE, DESCRIPTION_VALUE, DIFFICULTY_VALUE)
        val ALL_SUBTASK_FIELDS = listOf(TITLE_VALUE, DESCRIPTION_VALUE, DIFFICULTY_VALUE)
        private const val NUMBER_OF_FIELDS_FOR_TASK = 6
        private const val NUMBER_OF_FIELDS_FOR_SUBTASK = 3
        const val DATE_TIME_FORMAT = "dd MMM yyyy HH:mm"
    }
}