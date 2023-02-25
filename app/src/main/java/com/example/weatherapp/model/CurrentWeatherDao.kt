package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM CurrentWeather WHERE locationId = :locationId")
    fun getCurrentWeather(locationId: String): CurrentWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currentWeather: CurrentWeather)

    @Query("DELETE FROM CurrentWeather")
    suspend fun deleteAll()
}