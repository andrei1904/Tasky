package com.example.tasky.data.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tasky.data.model.enums.SubtaskStatus

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
            .filter { subtask -> subtask.status == SubtaskStatus.COMPLETE }
            .count()
    }

    fun getNumberOfSubtasks(): Int {
        return subtasks.size
    }
}