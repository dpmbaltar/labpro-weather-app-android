package com.example.weatherapp.model

import androidx.room.*
import com.google.gson.annotations.JsonAdapter
import java.util.*
import java.util.Calendar.HOUR

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocation::class,
            parentColumns = ["id"],
            childColumns = ["locationId"]
        )
    ],
    indices = [Index("locationId")]
)
data class CurrentWeather(
    @field:JsonAdapter(JsonAdapters.DateTimeAdapter::class)
    @PrimaryKey
    val time: Date,
    val temperature: Double,
    val apparentTemperature: Double,
    val precipitation: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val uv: Int,
    val isDay: Boolean,
    val conditionText: String,
    val conditionIcon: Int,
    val locationId: String? = null
) {
    fun isOld(): Boolean = Calendar.getInstance().apply { add(HOUR, -1) }.time > time
}