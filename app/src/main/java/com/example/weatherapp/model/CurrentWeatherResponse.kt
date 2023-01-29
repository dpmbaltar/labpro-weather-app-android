package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @field:SerializedName("location") val location: WeatherLocation,
    @field:SerializedName("current") val current: CurrentWeather
)
