package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class HourlyWeatherResponse(
    @field:SerializedName("location") val location: WeatherLocation,
    @field:SerializedName("hourly") val hourly: List<HourlyWeatherModel>
)
