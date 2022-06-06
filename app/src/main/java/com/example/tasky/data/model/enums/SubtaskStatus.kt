package com.example.tasky.data.model.enums

import com.squareup.moshi.Json

enum class SubtaskStatus(val value: String) {
    @Json(name = "New")
    NEW("New"),

    @Json(name = "InProgress")
    IN_PROGRESS("In Progress"),

    @Json(name = "Complete")
    COMPLETE("Complete")
}