package com.example.tasky.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

class CalendarManager {

    companion object {
        const val DATE_TIME_FORMAT = "dd MMM yyyy HH:mm"
    }

    fun openDatePickerDialog(editText: EditText, context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->

                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day, hour, minute)
                        calendar.timeInMillis

                        editText.setText(
                            getDateTimeFromMillis(
                                calendar.timeInMillis,
                                SimpleDateFormat(
                                    DATE_TIME_FORMAT,
                                    Locale.ENGLISH
                                )
                            )
                        )
                    },
                    startHour,
                    startMinute,
                    true
                ).show()
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }

    fun getDateTimeFromMillis(millis: Long, formatter: SimpleDateFormat): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        return formatter.format(calendar.time)
    }

}