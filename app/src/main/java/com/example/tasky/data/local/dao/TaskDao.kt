package com.example.tasky.data.local.dao

import androidx.room.*
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import io.reactivex.rxjava3.core.Single

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAll() : Single<List<Task>>

    @Query("SELECT * FROM task")
    fun getAllTasksWithSubtasks() : Single<List<TaskWithSubtasks>>

    @Query("SELECT * FROM task")
    fun getAllWithSubtasks() : List<TaskWithSubtasks>

    @Query("DELETE FROM task WHERE taskId = :id")
    fun deleteTaskById(id: Long): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubtasks(subtasks: List<Subtask>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertTaskWithSubtasks(task: Task, subtasks: List<Subtask>): Single<Long>
}