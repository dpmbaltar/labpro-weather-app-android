package com.example.weatherapp.model

import androidx.room.*

@Dao
interface WeatherLocationDao {

    @Query("SELECT * FROM WeatherLocation WHERE id = :id")
    fun getWeatherLocation(id: String): WeatherLocation?

    @Transaction
    @Query("SELECT * FROM WeatherLocation WHERE id = :id")
    fun getWeatherLocationAndCurrentWeather(id: String): CurrentWeatherResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherLocation: WeatherLocation)
}