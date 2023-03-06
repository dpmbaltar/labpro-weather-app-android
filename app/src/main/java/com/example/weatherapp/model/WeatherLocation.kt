package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index(value = arrayOf("latitude", "longitude"), unique = true)])
data class WeatherLocation(
    @PrimaryKey
    val id: String,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val timezone: String,
    val timezoneAbbreviation: String,
    val utcOffsetSeconds: Int
) {

    fun locationId(): String = buildId(latitude, longitude)

    companion object {
        fun buildId(latitude: Double, longitude: Double): String =
            String.format(Locale.US, "%.1f,%.1f", latitude, longitude)
    }
}