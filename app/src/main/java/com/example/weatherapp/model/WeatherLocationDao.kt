package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherLocationDao {

    @Query("SELECT * FROM WeatherLocation WHERE id = :id")
    fun getWeatherLocation(id: String): WeatherLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherLocation: WeatherLocation)
}