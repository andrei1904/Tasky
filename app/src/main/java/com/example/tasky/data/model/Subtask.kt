package com.example.tasky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.data.model.enums.Priority

@Entity(tableName = "subtask")
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    var subtaskId: Long = 0,
    var title: String = "",
    var description: String = "",
    var mainTaskId: Long = subtaskId
) {
}