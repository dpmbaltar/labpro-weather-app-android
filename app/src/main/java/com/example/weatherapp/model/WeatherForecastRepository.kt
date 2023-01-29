package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherForecastService
import retrofit2.Response
import javax.inject.Inject

class WeatherForecastRepository @Inject constructor(
    private val weatherForecastService: WeatherForecastService
) {

    suspend fun getConditions(): Response<List<WeatherCondition>> {
        return weatherForecastService.conditions()
    }

    suspend fun getCurrent(latitude: Double, longitude: Double): Response<CurrentWeatherResponse> {
        return weatherForecastService.current(latitude, longitude)
    }
}