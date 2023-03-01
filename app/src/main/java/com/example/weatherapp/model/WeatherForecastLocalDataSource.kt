package com.example.weatherapp.model

import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.model.WeatherLocation.Companion.buildId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar.DATE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastLocalDataSource @Inject constructor(
    private val locationDao: WeatherLocationDao,
    private val currentWeatherDao: CurrentWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend fun getWeatherLocation(latitude: Double, longitude: Double): WeatherLocation? =
        withContext(coroutineDispatcher) {
            locationDao.getWeatherLocation(buildId(latitude, longitude))
        }

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather? =
        withContext(coroutineDispatcher) {
            currentWeatherDao.getCurrentWeather(buildId(latitude, longitude))
        }

    suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) =
        withContext(coroutineDispatcher) {
            with(currentWeatherResponse) {
                val locationId = location.locationId()
                location.copy(id = locationId).let { locationDao.insert(it) }
                current.copy(locationId = locationId).let {
                    currentWeatherDao.deleteAll()
                    currentWeatherDao.insert(it)
                }
            }
        }

    suspend fun getDailyWeather(latitude: Double, longitude: Double): List<DailyWeather> =
        withContext(coroutineDispatcher) {
            dailyWeatherDao.getDailyWeather(buildId(latitude, longitude))
        }

    suspend fun insertDailyWeather(dailyWeatherResponse: DailyWeatherResponse) =
        withContext(coroutineDispatcher) {
            with(dailyWeatherResponse) {
                location.locationId().let { locationId ->
                    location.copy(id = locationId).let { locationDao.insert(it) }
                    daily.forEach { dailyWeather ->
                        dailyWeather.copy(
                            locationId = locationId,
                            timestamp = Calendar.getInstance()
                        ).let {
                            dailyWeatherDao.insert(it)
                        }
                    }
                }
            }
        }

    suspend fun getHistoricalDailyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        days: Int
    ): List<DailyWeather> =
        withContext(coroutineDispatcher) {
            val offset = Calendar.getInstance().apply {
                timeInMillis = date.timeInMillis
                add(DATE, days)
            }
            val from = if (days > 0) date else offset
            val to = if (days > 0) offset else date

            dailyWeatherDao.getHistoricalDailyWeather(buildId(latitude, longitude), from, to)
        }

    suspend fun insertHistoricalDailyWeather(dailyWeatherResponse: DailyWeatherResponse) =
        withContext(coroutineDispatcher) {
            with(dailyWeatherResponse) {
                location.locationId().let { locationId ->
                    location.copy(id = locationId).let { locationDao.insert(it) }
                    daily.forEach { dailyWeather ->
                        dailyWeather.copy(
                            locationId = locationId,
                            timestamp = Calendar.getInstance().apply { timeInMillis = 0 }
                        ).let {
                            dailyWeatherDao.insert(it)
                        }
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
            dailyWeatherDao: DailyWeatherDao,
            coroutineDispatcher: CoroutineDispatcher
        ) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastLocalDataSource(
                    locationDao,
                    currentWeatherDao,
                    dailyWeatherDao,
                    coroutineDispatcher
                ).also { instance = it }
            }
    }
}