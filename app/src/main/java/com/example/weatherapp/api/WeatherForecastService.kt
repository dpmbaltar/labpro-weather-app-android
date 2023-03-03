package com.example.weatherapp.api

import com.example.weatherapp.model.CurrentWeatherResult
import com.example.weatherapp.model.DailyWeatherResult
import com.example.weatherapp.model.HourlyWeatherResult
import com.example.weatherapp.util.API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherForecastService {

    @GET("/api/weather/current")
    suspend fun current(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<CurrentWeatherResult>

    @GET("/api/weather/daily")
    suspend fun daily(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<DailyWeatherResult>

    @GET("/api/weather/hourly/{year}/{month}/{day}")
    suspend fun hourly(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Response<HourlyWeatherResult>

    @GET("/api/weather/historical")
    suspend fun historical(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("date") date: String,
        @Query("days") days: Int,
        @Query("withLocation") withLocation: Boolean = true
    ): Response<DailyWeatherResult>

    companion object {
        private const val BASE_URL = API_BASE_URL

        fun create(): WeatherForecastService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherForecastService::class.java)
        }
    }
}