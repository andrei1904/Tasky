package com.example.tasky.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasky.data.local.dao.TaskDao
import com.example.tasky.data.model.SubTask
import com.example.tasky.data.model.Task

@Database(version = 1, entities = [Task::class, SubTask::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun TaskDao() : TaskDao
}