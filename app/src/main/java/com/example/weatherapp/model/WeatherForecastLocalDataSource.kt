package com.example.weatherapp.model

import android.util.Log
import com.example.weatherapp.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastLocalDataSource @Inject constructor(
    private val locationDao: WeatherLocationDao,
    private val currentWeatherDao: CurrentWeatherDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    // TODO: local data source
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeatherResponse? {
        return withContext(coroutineDispatcher) {
            try {
                val id = WeatherLocation.buildId(latitude, longitude)
                locationDao.getWeatherLocation(id)?.let { location ->
                    Log.d("getCurrentWeather", location.toString())
                    currentWeatherDao.getCurrentWeather(location.id)?.let { current ->
                        CurrentWeatherResponse(location, current)
                    }
                }
            } catch (e: Exception) {
                Log.d("getCurrentWeather", e.localizedMessage, e)
                null
            }
        }
    }

    suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) {
        withContext(coroutineDispatcher) {
            with (currentWeatherResponse.location) {
                locationDao.insert(copy(id = locationId())).let {
                    currentWeatherDao.deleteAll()
                    currentWeatherResponse.current.locationId = locationId()
                    currentWeatherDao.insert(currentWeatherResponse.current)
                }
            }
        }
    }

    companion object {

        @Volatile
        private var instance: WeatherForecastLocalDataSource? = null

        fun getInstance(
            locationDao: WeatherLocationDao,
            currentWeatherDao: CurrentWeatherDao,
            coroutineDispatcher: CoroutineDispatcher
        ) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastLocalDataSource(
                    locationDao,
                    currentWeatherDao,
                    coroutineDispatcher
                ).also { instance = it }
            }
    }
}