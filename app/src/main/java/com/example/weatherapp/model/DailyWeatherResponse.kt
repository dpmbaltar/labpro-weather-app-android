package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class DailyWeatherResponse(
    @field:SerializedName("location") val location: WeatherLocation,
    @field:SerializedName("daily") val daily: List<DailyWeather>
)
