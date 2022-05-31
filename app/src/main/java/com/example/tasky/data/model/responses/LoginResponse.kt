package com.example.tasky.data.model.responses

import com.example.tasky.data.model.entities.User
import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "accessToken")
    val accessToken: String,

    @Json(name = "refreshToekn")
    val refreshToekn: String,

    @Json(name = "user")
    val user: User
)