package com.example.weatherapp.model

import com.google.gson.annotations.JsonAdapter
import java.util.*

data class CurrentWeather(
    @field:JsonAdapter(JsonAdapters.DateTimeWithoutSecondsAdapter::class) val time: Date,
    val temperature: Double,
    val apparentTemperature: Double,
    val precipitation: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val uv: Int,
    val isDay: Boolean,
    val conditionText: String,
    val conditionIcon: Int
)