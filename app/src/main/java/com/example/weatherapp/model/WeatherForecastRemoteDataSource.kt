package com.example.weatherapp.model

import android.util.Log
import com.example.weatherapp.api.WeatherForecastService
import java.io.IOException
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
            val response = weatherService.current(latitude, longitude)
            if (response.isSuccessful) {
                return response.body()!!
            } else {
                val errorBody = response.errorBody() ?: ""
                throw Exception("Response error: $errorBody")
            }
        } catch (e: IOException) {
            throw Exception(e.localizedMessage)
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }

    suspend fun fetchDailyWeather(
        latitude: Double,
        longitude: Double
    ): DailyWeatherResponse {
        return try {
            weatherService.daily(latitude, longitude).let { it ->
                if (it.isSuccessful) {
                    it.body()!!
                } else {
                    it.errorBody().let { body ->
                        Log.d(TAG, body.toString())
                        throw Exception("Error ${it.code()}")
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage, e)
        }
    }

    companion object {

        private val TAG = WeatherForecastRemoteDataSource::class.java.simpleName

        @Volatile
        private var instance: WeatherForecastRemoteDataSource? = null

        fun getInstance(weatherService: WeatherForecastService) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRemoteDataSource(weatherService).also { instance = it }
            }
    }
}