package com.example.tasky.data.model.enums

import com.squareup.moshi.Json

enum class Difficulty(val value: String) {
    @Json(name = "Easy")
    EASY("Easy"),

    @Json(name = "Medium")
    MEDIUM("Medium"),

    @Json(name = "Hard")
    HARD("Hard")
}