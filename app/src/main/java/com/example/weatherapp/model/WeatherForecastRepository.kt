package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherForecastService
import retrofit2.Response
import javax.inject.Inject

class WeatherForecastRepository @Inject constructor(
    private val weatherForecastService: WeatherForecastService
) {

    suspend fun getCurrent(latitude: Double, longitude: Double): Response<CurrentWeatherResponse> {
        return weatherForecastService.current(latitude, longitude)
    }

    suspend fun getDaily(latitude: Double, longitude: Double): Response<DailyWeatherResponse> {
        return weatherForecastService.daily(latitude, longitude)
    }
}