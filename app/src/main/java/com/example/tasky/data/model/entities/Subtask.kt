package com.example.tasky.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtask")
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    var subtaskId: Long = 0,
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false,
    var mainTaskId: Long = subtaskId
)