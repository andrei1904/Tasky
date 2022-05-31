package com.example.tasky.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.tasky.data.model.entities.Task
import io.reactivex.rxjava3.core.Single

@Dao
interface SubtaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubtask(task: Task): Single<Long>
}