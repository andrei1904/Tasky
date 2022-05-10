package com.example.tasky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.data.model.enums.Priority

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    var domain: String = "",
    var title: String = "",
    var priority: Priority = Priority.LOW,
    var deadline: Long = 0,
    var progress: Int = 0,
    var description: String = ""
)
