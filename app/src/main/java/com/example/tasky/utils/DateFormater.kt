package com.example.tasky.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormater {

    private val simpleDateFormat = SimpleDateFormat(
        CalendarManager.DATE_TIME_FORMAT,
        Locale.ENGLISH
    )

    fun getDateTimeFromMillis(
        millis: Long,
        formatter: SimpleDateFormat = simpleDateFormat
    ): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        return formatter.format(calendar.time)
    }
}