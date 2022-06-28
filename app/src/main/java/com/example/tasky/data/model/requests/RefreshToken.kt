package com.example.tasky.data.model.requests

data class RefreshToken (
    val accessToken: String,

    val refreshToken: String
)