package com.example.weatherapp.model

data class DailyWeather(
    val time: String,
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
    val weatherCode: Int
)
