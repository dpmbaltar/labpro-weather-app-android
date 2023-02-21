package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "weather_locations",
    indices = [Index("latitude", "longitude", unique = true)]
)
data class WeatherLocation(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val timezone: String,
    val timezoneAbbreviation: String,
    val utcOffsetSeconds: Int,
    val timestamp: Calendar = Calendar.getInstance()
)