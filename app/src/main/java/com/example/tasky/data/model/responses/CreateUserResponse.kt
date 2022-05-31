package com.example.tasky.data.model.responses

import com.squareup.moshi.Json

data class CreateUserResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "username")
    val username: String
) {
}