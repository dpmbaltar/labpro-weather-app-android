package com.example.weatherapp.api

import com.example.weatherapp.model.Condition
import com.example.weatherapp.model.WeatherCurrentResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastService {

    @GET("/api/weather/conditions")
    suspend fun conditions(): Response<List<Condition>>

    @GET("/api/weather/current")
    suspend fun current(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<WeatherCurrentResponse>

    companion object {
        private const val BASE_URL = "http://192.168.0.239:9000/"

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