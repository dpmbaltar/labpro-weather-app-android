package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherLocationDao {

    @Query("SELECT * FROM weather_locations WHERE id = :id")
    fun getWeatherLocation(id: String): WeatherLocation?

    @Query("SELECT * FROM weather_locations WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    fun getWeatherLocation(lat: Double, lon: Double): WeatherLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherLocation: WeatherLocation)
}