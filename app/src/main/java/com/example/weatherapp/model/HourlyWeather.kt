package com.example.weatherapp.model

data class HourlyWeather(
    val time: List<String>,
    val temperature: List<Double>,
    val apparentTemperature: List<Double>,
    val precipitation: List<Double>,
    val relativeHumidity: List<Double>,
    val dewPoint: List<Double>,
    val cloudCover: List<Double>,
    val visibility: List<Double>,
    val surfacePressure: List<Double>,
    val windSpeed: List<Double>,
    val windDirection: List<Double>,
    val windGusts: List<Double>,
    val conditionText: List<String>,
    val conditionIcon: List<Int>
)
