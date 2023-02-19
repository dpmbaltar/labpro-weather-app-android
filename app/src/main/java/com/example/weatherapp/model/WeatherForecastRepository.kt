package com.example.weatherapp.model

import android.annotation.SuppressLint
import com.example.weatherapp.api.WeatherForecastService
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
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

    suspend fun getHourly(
        latitude: Double,
        longitude: Double,
        date: Calendar
    ): Response<HourlyWeatherResponse> {
        return weatherForecastService.hourly(
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH) + 1,
            date.get(Calendar.DAY_OF_MONTH),
            latitude,
            longitude
        )
    }

    suspend fun getHistorical(
        latitude: Double,
        longitude: Double,
        date: Date,
        days: Int
    ): Response<DailyWeatherResponse> {
        return weatherForecastService.historical(
            latitude,
            longitude,
            dateFormat.format(date),
            days.coerceAtLeast(-7).coerceAtMost(7)
        )
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    }
}