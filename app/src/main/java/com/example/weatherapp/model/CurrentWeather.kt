package com.example.weatherapp.model

data class CurrentWeather(
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val precipitation: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val uv: Int,
    val isDay: Boolean,
    val weatherCode: Int
)