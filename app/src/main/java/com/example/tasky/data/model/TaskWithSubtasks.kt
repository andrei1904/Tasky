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
}