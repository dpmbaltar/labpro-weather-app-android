package com.example.weatherapp.model

import androidx.room.*
import com.google.gson.annotations.JsonAdapter
import java.util.*
import java.util.Calendar.HOUR

@Entity(
    tableName = "current_weather",
    foreignKeys = [
        ForeignKey(
            entity = WeatherLocation::class,
            parentColumns = ["id"],
            childColumns = ["location_id"]
        )
    ],
    indices = [Index("location_id")]
)
data class CurrentWeather(
    @field:JsonAdapter(JsonAdapters.DateTimeWithoutSecondsAdapter::class)
    @PrimaryKey
    @ColumnInfo(name = "time")
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
    @ColumnInfo(name = "location_id")
    val locationId: String? = null
) {

    fun isOld() = Calendar.getInstance().apply { add(HOUR, -1) }.time > time
}