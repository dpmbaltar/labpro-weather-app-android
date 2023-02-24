package com.example.weatherapp.model

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.TEXT
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "weather_locations",
    indices = [Index(value = arrayOf("latitude", "longitude"), unique = true)]
)
data class WeatherLocation(
    @PrimaryKey
    @ColumnInfo(name = "id", typeAffinity = TEXT)
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

    fun locationId() = buildId(latitude, longitude)

    companion object {
        fun buildId(latitude: Double, longitude: Double) =
            String.format(Locale.US, "%.2f,%.2f", latitude, longitude)
    }
}