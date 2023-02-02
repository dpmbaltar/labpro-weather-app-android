package com.example.weatherapp.model

data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val precipitation: Double,
    val windSpeed: Double,
    val conditionIcon: Int
)
