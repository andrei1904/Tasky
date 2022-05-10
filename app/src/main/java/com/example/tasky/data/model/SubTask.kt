package com.example.tasky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtask")
data class SubTask(
    @PrimaryKey(autoGenerate = true)
    var subtaskId: Long = -1,
    val title: String,
    val description: String,
    val mainTaskId: Long
) {
}