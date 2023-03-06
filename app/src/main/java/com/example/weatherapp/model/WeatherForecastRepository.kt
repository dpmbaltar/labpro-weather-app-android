package com.example.weatherapp.model

import com.example.weatherapp.model.WeatherLocation.Companion.buildId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class WeatherForecastRepository @Inject constructor(
    private val localWeather: WeatherForecastLocalDataSource,
    private val remoteWeather: WeatherForecastRemoteDataSource
) {

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Flow<CurrentWeatherResult> = flow {
        localWeather.getWeatherLocationAndCurrentWeather(latitude, longitude)?.let {
            with(it) {
                emit(CurrentWeatherResult(location, current))
                if (!current.isOld()) return@flow
            }
        }

        remoteWeather.fetchCurrentWeather(latitude, longitude).let { result ->
            result.copy(
                location = result.location.copy(
                    id = buildId(latitude, longitude),
                    latitude = latitude,
                    longitude = longitude
                )
            ).let { new ->
                localWeather.insertCurrentWeather(new)
                emit(new)
            }
        }
    }

    suspend fun getDailyWeather(
        latitude: Double,
        longitude: Double
    ): Flow<DailyWeatherResult> = flow {
        localWeather.getWeatherLocation(latitude, longitude)?.let { location ->
            localWeather.getDailyWeather(latitude, longitude).let { daily ->
                if (daily.isNotEmpty()) {
                    emit(DailyWeatherResult(location, daily))
                    if (daily.first().isOld().not())
                        return@flow
                    else
                        localWeather.deleteHourlyWeather()
                }
            }
        }

        remoteWeather.fetchDailyWeather(latitude, longitude).let { result ->
            result.copy(
                location = result.location.copy(
                    id = buildId(latitude, longitude),
                    latitude = latitude,
                    longitude = longitude
                )
            ).let {
                localWeather.insertDailyWeather(it)
                emit(it)
            }
        }
    }

    suspend fun getHistoricalDailyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        days: Int
    ): Flow<DailyWeatherResult> = flow {
        localWeather.getWeatherLocation(latitude, longitude)?.let { location ->
            localWeather.getHistoricalDailyWeather(latitude, longitude, date, days).let { daily ->
                if (daily.size == days.absoluteValue) {
                    emit(DailyWeatherResult(location, daily))
                    return@flow
                }
            }
        }

        remoteWeather.fetchHistoricalDailyWeather(latitude, longitude, date, days).let { result ->
            result.copy(
                location = result.location.copy(
                    id = buildId(latitude, longitude),
                    latitude = latitude,
                    longitude = longitude
                )
            ).let {
                localWeather.insertHistoricalDailyWeather(it)
                emit(it)
            }
        }
    }

    suspend fun getHourlyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar
        ): Flow<List<HourlyWeather>> = flow {
        localWeather.getWeatherLocation(latitude, longitude)?.let {
            localWeather.getHourlyWeather(latitude, longitude, date.time).let { daily ->
                if (daily.isNotEmpty())
                    return@flow emit(daily)
            }
        }
        remoteWeather.fetchHourlyWeather(latitude, longitude, date).let {
            emit(it)
            it.forEach { hourlyWeather ->
                localWeather.insertHourlyWeather(latitude, longitude, date.time, hourlyWeather)
            }
        }
    }

    companion object {

        @Volatile
        private var instance: WeatherForecastRepository? = null

        fun getInstance(
            weatherLocalDataSource: WeatherForecastLocalDataSource,
            weatherRemoteDataSource: WeatherForecastRemoteDataSource
        ) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRepository(
                    weatherLocalDataSource,
                    weatherRemoteDataSource
                ).also { instance = it }
            }
    }
}