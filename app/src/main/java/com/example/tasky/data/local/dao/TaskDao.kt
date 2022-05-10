package com.example.tasky.data.local.dao

import androidx.room.*
import com.example.tasky.data.model.Task
import com.example.tasky.data.model.TaskWithSubtasks
import io.reactivex.rxjava3.core.Single

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAll() : Single<List<Task>>

    @Query("SELECT * FROM task")
    fun getAllWithSubtasks() : List<TaskWithSubtasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task): Single<Long>

    @Query("DELETE FROM task WHERE taskId = :id")
    fun deleteTaskById(id: Long): Single<Int>
}