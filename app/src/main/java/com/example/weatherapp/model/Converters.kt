package com.example.weatherapp.model

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun timestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }

    @TypeConverter
    fun doubleToString(value: Double): String = String.format(Locale.US, "%.2f", value)

    @TypeConverter
    fun stringToDouble(value: String): Double = value.toDouble()
}