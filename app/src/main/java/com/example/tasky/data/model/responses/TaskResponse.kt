package com.example.tasky.data.model.responses

import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.enums.Priority
import com.squareup.moshi.Json

data class TaskResponse(
    @Json(name = "taskId")
    var taskId: Long,

    @Json(name = "domain")
    var domain: String,

    @Json(name = "title")
    var title: String,

    @Json(name = "priority")
    var priority: Priority,

    @Json(name = "deadline")
    var deadline: Long,

    @Json(name = "progress")
    var progress: Int,

    @Json(name = "description")
    var description: String,

    @Json(name = "imposedDeadline")
    var imposedDeadline: Long,

    @Json(name = "subtasks")
    var subtasks: List<Subtask>
)