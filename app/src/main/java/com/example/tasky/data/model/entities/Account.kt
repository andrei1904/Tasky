package com.example.tasky.data.model.entities

import com.squareup.moshi.Json

data class Account(
    @Json(name = "username")
    var username: String = "",

    @Json(name = "password")
    var password: String = ""
)