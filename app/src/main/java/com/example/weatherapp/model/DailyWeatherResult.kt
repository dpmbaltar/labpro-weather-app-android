package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class DailyWeatherResult(
    @field:SerializedName("location") val location: WeatherLocation,
    @field:SerializedName("daily") val daily: List<DailyWeather>
)
