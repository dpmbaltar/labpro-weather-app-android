package com.example.weatherapp.model

import androidx.room.*
import com.google.gson.annotations.JsonAdapter
import java.util.*

@Entity(
    primaryKeys = ["time", "locationId"],
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocation::class,
            parentColumns = ["id"],
            childColumns = ["locationId"]
        )
    ],
    indices = [Index("locationId")]
)
data class DailyWeather(
    @field:JsonAdapter(JsonAdapters.DateAdapter::class)
    val time: Date,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val apparentTemperatureMax: Double,
    val apparentTemperatureMin: Double,
    val sunrise: String,
    val sunset: String,
    val precipitationSum: Double,
    val precipitationHours: Double,
    val windSpeedMax: Double,
    val windGustsMax: Double,
    val windDirection: Double,
    val conditionText: String,
    val conditionIcon: Int,
    val locationId: String,
    val timestamp: Calendar = Calendar.getInstance()
) {
    fun isOld(): Boolean = Calendar.getInstance()
        .apply { add(Calendar.HOUR, -1) }.timeInMillis > timestamp.timeInMillis
}