package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.util.ConnectionException
import com.example.weatherapp.util.RemoteResponseException
import com.google.gson.Gson
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastRemoteDataSource @Inject constructor(
    private val weatherService: WeatherForecastService
) {

    // TODO: remote data source
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

    companion object {

        @Volatile
        private var instance: WeatherForecastRemoteDataSource? = null

        fun getInstance(weatherService: WeatherForecastService) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRemoteDataSource(weatherService).also { instance = it }
            }
    }
}