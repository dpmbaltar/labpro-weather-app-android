package com.example.weatherapp.model

import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.model.WeatherLocation.Companion.buildId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastLocalDataSource @Inject constructor(
    private val locationDao: WeatherLocationDao,
    private val currentWeatherDao: CurrentWeatherDao,
    private val dailyWeatherDao: DailyWeatherDao,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    // TODO: local data source
    suspend fun getWeatherLocation(latitude: Double, longitude: Double): WeatherLocation? =
        withContext(coroutineDispatcher) {
            locationDao.getWeatherLocation(buildId(latitude, longitude))
        }

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather? =
        withContext(coroutineDispatcher) {
            currentWeatherDao.getCurrentWeather(buildId(latitude, longitude))
        }

    suspend fun getCurrentWeather(weatherLocation: WeatherLocation): CurrentWeather? =
        withContext(coroutineDispatcher) {
            currentWeatherDao.getCurrentWeather(weatherLocation.locationId())
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

    suspend fun getDailyWeather(weatherLocation: WeatherLocation): List<DailyWeather> =
        withContext(coroutineDispatcher) {
            dailyWeatherDao.getDailyWeather(weatherLocation.locationId())
        }

    suspend fun insertDailyWeather(dailyWeatherResponse: DailyWeatherResponse) =
        withContext(coroutineDispatcher) {
            with(dailyWeatherResponse) {
                val currentTime = Calendar.getInstance()
                val locationId = location.locationId()

                location.copy(id = locationId).let { locationDao.insert(it) }
                //dailyWeatherDao.deleteOld(locationId, currentTime.apply { add(HOUR, -1) })
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

    suspend fun getHistoricalDailyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        days: Int
    ): List<DailyWeather> =
        withContext(coroutineDispatcher) {
            dailyWeatherDao.getHistoricalDailyWeather(buildId(latitude, longitude), date, days)
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