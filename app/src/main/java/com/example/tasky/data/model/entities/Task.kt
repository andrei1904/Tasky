package com.example.tasky.data.model.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.data.model.enums.Priority
import com.example.tasky.data.model.enums.ProgressBarType
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
    var difficulty: Difficulty = Difficulty.EASY,
    var progressBarType: ProgressBarType = ProgressBarType.TYPE1
)
