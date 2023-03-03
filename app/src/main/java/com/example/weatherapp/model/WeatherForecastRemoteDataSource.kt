package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.util.ConnectionException
import com.example.weatherapp.util.RemoteResponseException
import com.example.weatherapp.util.isoDate
import com.example.weatherapp.util.isoDateTimeSimple
import com.google.gson.Gson
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastRemoteDataSource @Inject constructor(
    private val weatherService: WeatherForecastService
) {

    suspend fun fetchCurrentWeather(
        latitude: Double,
        longitude: Double
    ): CurrentWeatherResult = try {
        weatherService.current(latitude, longitude).let { response ->
            if (response.isSuccessful) {
                response.body()!!
            } else {
                val errorBody: String = response.errorBody()?.string() ?: ""
                val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                throw RemoteResponseException("Response error", json)
            }
        }
    } catch (e: Exception) {
        throw catchException(e)
    }

    suspend fun fetchDailyWeather(
        latitude: Double,
        longitude: Double
    ): DailyWeatherResult = try {
        weatherService.daily(latitude, longitude).let { response ->
            if (response.isSuccessful) {
                response.body()!!
            } else {
                val errorBody: String = response.errorBody()?.string() ?: ""
                val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                throw RemoteResponseException("Response error", json)
            }
        }
    } catch (e: Exception) {
        throw catchException(e)
    }

    suspend fun fetchHourlyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar
    ): List<HourlyWeather> = try {
        with(date) {
            val year = get(Calendar.YEAR)
            val month = get(Calendar.MONTH).plus(1)
            val day = get(Calendar.DATE)

            weatherService.hourly(year, month, day, latitude, longitude).let { response ->
                if (response.isSuccessful) {
                    response.body()!!.let {
                        val hourlyValues = it.hourly[0]
                        val hourlyItems = arrayListOf<HourlyWeather>()

                        for (i in 0..23) {
                            hourlyItems.add(
                                HourlyWeather(
                                    time = hourlyValues.time[i].isoDateTimeSimple(),
                                    temperature = hourlyValues.temperature[i],
                                    precipitation = hourlyValues.precipitation[i],
                                    windSpeed = hourlyValues.windSpeed[i],
                                    conditionText = hourlyValues.conditionText[i],
                                    conditionIcon = hourlyValues.conditionIcon[i]
                                )
                            )
                        }

                        return hourlyItems
                    }
                } else {
                    val errorBody: String = response.errorBody()?.string() ?: ""
                    val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                    throw RemoteResponseException("Response error", json)
                }
            }
        }
    } catch (e: Exception) {
        throw catchException(e)
    }

    suspend fun fetchHistoricalDailyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        days: Int
    ): DailyWeatherResult = try {
        weatherService.historical(
            latitude,
            longitude,
            date.coerceAtMost(Calendar.getInstance().apply { add(Calendar.DATE, -7) }).isoDate(),
            days.coerceAtLeast(-7).coerceAtMost(7)
        ).let { response ->
            if (response.isSuccessful) {
                response.body()!!
            } else {
                val errorBody: String = response.errorBody()?.string() ?: ""
                val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                throw RemoteResponseException("Response error", json)
            }
        }
    } catch (e: Exception) {
        throw catchException(e)
    }

    private fun catchException(exception: Exception): Exception = when (exception) {
        is ConnectException,
        is SocketException,
        is SocketTimeoutException,
        is IOException -> ConnectionException(exception.message)
        else -> Exception(exception.message)
    }

    companion object {

        @Volatile
        private var instance: WeatherForecastRemoteDataSource? = null

        fun getInstance(weatherService: WeatherForecastService) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRemoteDataSource(weatherService).also { instance = it }
            }
    }
}