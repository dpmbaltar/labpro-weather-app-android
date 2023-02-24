package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastLocalDataSource @Inject constructor(
    private val locationDao: WeatherLocationDao,
    private val currentWeatherDao: CurrentWeatherDao
) {

    // TODO: local data source
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Flow<CurrentWeatherResponse> {
        var result: Flow<CurrentWeatherResponse> = flow {  }
        val location = locationDao.getWeatherLocation(latitude, longitude).firstOrNull()

        return result
    }

    companion object {

        @Volatile
        private var instance: WeatherForecastLocalDataSource? = null

        fun getInstance(
            locationDao: WeatherLocationDao,
            currentWeatherDao: CurrentWeatherDao
        ) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastLocalDataSource(
                    locationDao,
                    currentWeatherDao
                ).also { instance = it }
            }
    }
}