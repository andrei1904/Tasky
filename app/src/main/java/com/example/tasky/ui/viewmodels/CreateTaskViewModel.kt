package com.example.tasky.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tasky.data.model.Task
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.ui.repositories.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val mTasksRepository: TasksRepository
) : ViewModel() {

    companion object {
        const val DOMAIN_VALUE = "domain"
        const val TITLE_VALUE = "title"
        const val DEADLINE_VALUE = "deadline"
        const val PRIORITY_VALUE = "priority"
        const val DESCRIPTION_VALUE = "description"
        val ALL_FIELDS = listOf(DOMAIN_VALUE, TITLE_VALUE, DEADLINE_VALUE, PRIORITY_VALUE, DESCRIPTION_VALUE)
        private const val NUMBER_OF_FIELDS = 5
        const val DATE_TIME_FORMAT = "dd MMM yyyy hh:mm";
    }

    val values = Companion

    private val completedFields: MutableSet<String> = mutableSetOf()

    private val task: Task = Task()

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
            completedFields.remove(type)
        } else {
            completedFields.add(type)
        }
    }

    fun isTaskValid(): Boolean {
        if (completedFields.size == NUMBER_OF_FIELDS) {
            return true
        }
        return false
    }

    fun getCompltedFields() : MutableSet<String> {
        return completedFields
    }
}