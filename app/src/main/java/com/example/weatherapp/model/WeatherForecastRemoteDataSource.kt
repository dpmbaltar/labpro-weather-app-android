package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.util.ConnectionException
import com.example.weatherapp.util.RemoteResponseException
import com.example.weatherapp.util.isoDate
import com.google.gson.Gson
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import java.util.Calendar.DATE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastRemoteDataSource @Inject constructor(
    private val weatherService: WeatherForecastService
) {

    suspend fun fetchCurrentWeather(
        latitude: Double,
        longitude: Double
    ): CurrentWeatherResponse {
        try {
            weatherService.current(latitude, longitude).let {
                if (it.isSuccessful) {
                    return it.body()!!
                } else {
                    val errorBody: String = it.errorBody()?.string() ?: ""
                    val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                    throw RemoteResponseException("Response error", json)
                }
            }
        } catch (e: SocketTimeoutException) {
            throw ConnectionException(e.message)
        } catch (e: IOException) {
            throw ConnectionException(e.message)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    suspend fun fetchDailyWeather(
        latitude: Double,
        longitude: Double
    ): DailyWeatherResponse {
        return try {
            weatherService.daily(latitude, longitude).let {
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    val errorBody: String = it.errorBody()?.string() ?: ""
                    val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                    throw RemoteResponseException("Response error", json)
                }
            }
        } catch (e: SocketTimeoutException) {
            throw ConnectionException(e.message)
        } catch (e: IOException) {
            throw ConnectionException(e.message)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    suspend fun fetchHistoricalDailyWeather(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        days: Int
    ): DailyWeatherResponse {
        return try {
            weatherService.historical(
                latitude,
                longitude,
                date.coerceAtMost(Calendar.getInstance().apply { add(DATE, -7) }).isoDate(),
                days.coerceAtLeast(-7).coerceAtMost(7)
            ).let {
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    val errorBody: String = it.errorBody()?.string() ?: ""
                    val json = Gson().fromJson<Map<String, Any>>(errorBody, Map::class.java)
                    throw RemoteResponseException("Response error", json)
                }
            }
        } catch (e: SocketTimeoutException) {
            throw ConnectionException(e.message)
        } catch (e: IOException) {
            throw ConnectionException(e.message)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
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