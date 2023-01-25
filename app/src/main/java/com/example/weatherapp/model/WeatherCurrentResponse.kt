package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class WeatherCurrentResponse(
    @field:SerializedName("location") val location: Location,
    @field:SerializedName("current") val current: Current
)
