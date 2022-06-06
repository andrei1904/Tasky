package com.example.tasky.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.data.model.enums.Status

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = false)
    var taskId: Long = 0,
    var domain: String = "",
    var title: String = "",
    var priority: Priority = Priority.LOW,
    var deadline: Long = 0,
    var progress: Int = 0,
    var description: String = "",
    var imposedDeadline: Long = 0,
    var status: Status = Status.NEW,
    var spentTime: Long = 0,
    var difficulty: Difficulty = Difficulty.EASY
) {
    fun updateStatus() {
        status = if (progress == 0) {
            Status.NEW
        } else {
            if (progress < 100) {
                Status.IN_PROGRESS
            } else {
                Status.COMPLETE
            }
        }
    }
}
