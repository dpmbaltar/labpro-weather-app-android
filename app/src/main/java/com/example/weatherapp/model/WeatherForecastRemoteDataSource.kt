package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherForecastService
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    companion object {

        @Volatile
        private var instance: WeatherForecastRemoteDataSource? = null

        fun getInstance(weatherService: WeatherForecastService) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastRemoteDataSource(weatherService).also { instance = it }
            }
    }
}