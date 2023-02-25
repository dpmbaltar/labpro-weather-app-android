package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyWeatherDao {

    @Query("SELECT * FROM DailyWeather WHERE locationId = :locationId")
    fun getDailyWeather(locationId: String): List<DailyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyWeather: DailyWeather)

    @Query("DELETE FROM DailyWeather WHERE locationId = :locationId")
    suspend fun delete(locationId: String)
}