package com.example.weatherapp.model

import android.annotation.SuppressLint
import com.example.weatherapp.api.WeatherForecastService
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("MissingPermission")
class WeatherForecastRepository @Inject constructor(
    private val weatherService: WeatherForecastService,
    private val locationProviderClient: FusedLocationProviderClient,
    private val localWeather: WeatherForecastLocalDataSource,
    private val remoteWeather: WeatherForecastRemoteDataSource
) {

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Flow<CurrentWeatherResponse> = flow {
        localWeather.getWeatherLocation(latitude, longitude)?.let { location ->
            localWeather.getCurrentWeather(location)?.let { current ->
                emit(CurrentWeatherResponse(location, current))
                if (!current.isOld()) return@flow
            }
        }

        remoteWeather.fetchCurrentWeather(latitude, longitude).let {
            localWeather.insertCurrentWeather(it)
            emit(it)
        }
    }

    suspend fun getDailyWeather(
        latitude: Double,
        longitude: Double
    ): Flow<DailyWeatherResponse> = flow {
        remoteWeather.fetchDailyWeather(latitude, longitude).let {
            emit(it)
        }
    }

    suspend fun getCurrent(latitude: Double, longitude: Double): Response<CurrentWeatherResponse> {
        return weatherService.current(latitude, longitude)
    }

    suspend fun getDaily(latitude: Double, longitude: Double): Response<DailyWeatherResponse> {
        return weatherService.daily(latitude, longitude)
    }

    suspend fun getHourly(
        latitude: Double,
        longitude: Double,
        date: Calendar
    ): Response<HourlyWeatherResponse> {
        return weatherService.hourly(
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
        days: Int = -7
    ): Response<DailyWeatherResponse> {
        return weatherService.historical(
            latitude,
            longitude,
            dateFormat.format(date),
            days.coerceAtLeast(-7).coerceAtMost(7)
        )
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        @Volatile
        private var instance: WeatherForecastRepository? = null

        fun getInstance(
            weatherService: WeatherForecastService,
            locationProviderClient: FusedLocationProviderClient,
            weatherLocalDataSource: WeatherForecastLocalDataSource,
            weatherRemoteDataSource: WeatherForecastRemoteDataSource
        ) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRepository(
                    weatherService,
                    locationProviderClient,
                    weatherLocalDataSource,
                    weatherRemoteDataSource
                ).also { instance = it }
            }
    }
}