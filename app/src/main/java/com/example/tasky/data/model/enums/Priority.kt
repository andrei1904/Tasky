package com.example.tasky.data.model.enums

import com.squareup.moshi.Json

enum class Priority(val value: String) {
    @Json(name = "Low")
    LOW("Low"),

    @Json(name = "Medium")
    MEDIUM("Medium"),

    @Json(name = "High")
    HIGH("High")
}