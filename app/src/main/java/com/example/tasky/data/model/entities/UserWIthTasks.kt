package com.example.tasky.data.model.entities

import com.squareup.moshi.Json

data class UserWIthTasks(
    @Json(name = "id")
    val id: Long,

    @Json(name = "username")
    val username: String,

    @Json(name = "firstName")
    val firstName: String?,

    @Json(name = "lastName")
    val lastName: String?,

    @Json(name = "tasks")
    val tasks: List<TaskWithSubtasks>
)