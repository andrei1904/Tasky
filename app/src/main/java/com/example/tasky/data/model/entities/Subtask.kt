package com.example.tasky.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.data.model.enums.SubtaskStatus

@Entity(tableName = "subtask")
data class Subtask(
    @PrimaryKey(autoGenerate = false)
    var subtaskId: Long = 0,
    var title: String = "",
    var description: String = "",
    var status: SubtaskStatus = SubtaskStatus.NEW,
    var mainTaskId: Long = subtaskId,
    var difficulty: Difficulty = Difficulty.EASY
)