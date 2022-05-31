package com.example.tasky.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasky.data.local.dao.SubtaskDao
import com.example.tasky.data.local.dao.TaskDao
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task

@Database(version = 4, entities = [Task::class, Subtask::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun TaskDao() : TaskDao
    abstract fun SubtaskDao() : SubtaskDao
}