package com.example.weatherapp.model

data class Current(
    val time: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val weatherCode: Int,
    val uv: Int,
    val isDay: Boolean
)