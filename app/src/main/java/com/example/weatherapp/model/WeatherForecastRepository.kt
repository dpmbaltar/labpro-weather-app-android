package com.example.weatherapp.model

import android.annotation.SuppressLint
import com.example.weatherapp.api.WeatherForecastService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
@SuppressLint("MissingPermission")
class WeatherForecastRepository @Inject constructor(
    private val weatherService: WeatherForecastService,
    private val localWeather: WeatherForecastLocalDataSource,
    private val remoteWeather: WeatherForecastRemoteDataSource
) {

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Flow<CurrentWeatherResponse> = flow {
        localWeather.getWeatherLocation(latitude, longitude)?.let { location ->
            localWeather.getCurrentWeather(latitude, longitude)?.let { current ->
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
        localWeather.getWeatherLocation(latitude, longitude)?.let { location ->
            localWeather.getDailyWeather(latitude, longitude).let { daily ->
                if (daily.isNotEmpty()) {
                    emit(DailyWeatherResponse(location, daily))
                    if (daily.first().isOld().not())
                        return@flow
                }
            }
        }

        remoteWeather.fetchDailyWeather(latitude, longitude).let {
            localWeather.insertDailyWeather(it)
            emit(it)
        }
    }

    suspend fun getHistoricalDailyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        days: Int
    ): Flow <DailyWeatherResponse> = flow {
        localWeather.getWeatherLocation(latitude, longitude)?.let { location ->
            localWeather.getHistoricalDailyWeather(latitude, longitude, date, days).let { daily ->
                if (daily.size == days.absoluteValue) {
                    emit(DailyWeatherResponse(location, daily))
                    return@flow
                }
            }
        }

        remoteWeather.fetchHistoricalDailyWeather(latitude, longitude, date, days).let {
            localWeather.insertHistoricalDailyWeather(it)
            emit(it)
        }
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

    companion object {

        @Volatile
        private var instance: WeatherForecastRepository? = null

        fun getInstance(
            weatherService: WeatherForecastService,
            weatherLocalDataSource: WeatherForecastLocalDataSource,
            weatherRemoteDataSource: WeatherForecastRemoteDataSource
        ) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRepository(
                    weatherService,
                    weatherLocalDataSource,
                    weatherRemoteDataSource
                ).also { instance = it }
            }
    }
}