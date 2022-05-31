package com.example.tasky.data.model.entities

data class AdapterData(
    val viewType: Int,
    val task: Task?,
    val subtasks: Subtask?
)
