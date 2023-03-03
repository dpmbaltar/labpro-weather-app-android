package com.example.weatherapp.model

import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

data class CurrentWeatherResult(

    @field:SerializedName("location")
    @Embedded
    val location: WeatherLocation,

    @field:SerializedName("current")
    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val current: CurrentWeather
)
