package com.example.tasky.utils.preferences

interface IPreferenceHelper {

    fun setToken(token: String)

    fun getToken(token: String): String

    fun setUserId(userId: Long)

    fun getUserId(userId: Long): String

    fun clearPrefs()
}