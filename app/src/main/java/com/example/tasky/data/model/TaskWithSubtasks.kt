package com.example.tasky.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithSubtasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "mainTaskId"
    )
    val subtasks: List<Subtask>
) {

    fun getNumberOfCompletedSubtasks(): Long {
        return subtasks.stream()
            .filter { subtask -> subtask.isCompleted }
            .count()
    }

    fun getNumberOfSubtasks(): Int {
        return subtasks.size
    }
}