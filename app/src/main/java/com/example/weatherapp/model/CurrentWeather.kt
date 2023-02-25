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
    @field:JsonAdapter(JsonAdapters.DateTimeAdapter::class)
    @ColumnInfo(name = "time") @PrimaryKey val time: Date,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "apparent_temperature") val apparentTemperature: Double,
    @ColumnInfo(name = "precipitation") val precipitation: Double,
    @ColumnInfo(name = "humidity") val humidity: Double,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_direction") val windDirection: Double,
    @ColumnInfo(name = "uv") val uv: Int,
    @ColumnInfo(name = "is_day") val isDay: Boolean,
    @ColumnInfo(name = "condition_text") val conditionText: String,
    @ColumnInfo(name = "condition_icon") val conditionIcon: Int,
    @ColumnInfo(name = "location_id") val locationId: String? = null
) {
    fun isOld(): Boolean = Calendar.getInstance().apply { add(HOUR, -1) }.time > time
}