package com.example.tasky.data.model

data class AdapterData(
    val viewType: Int,
    val task: Task?,
    val subtasks: Subtask?
)
