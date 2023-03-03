package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DailyWeather::class,
            parentColumns = ["time", "locationId"],
            childColumns = ["date", "locationId"]
        )
    ],
    indices = [Index("date", "locationId")]
)
data class HourlyWeather(
    @PrimaryKey
    val time: Date,
    val temperature: Double,
    val precipitation: Double,
    val windSpeed: Double,
    val conditionText: String,
    val conditionIcon: Int,
    val date: Date? = null,
    val locationId: String? = null
)
