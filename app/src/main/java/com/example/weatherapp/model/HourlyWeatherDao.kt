package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface HourlyWeatherDao {

    @Query("SELECT * FROM HourlyWeather WHERE date = :date AND locationId = :locationId")
    fun getHourlyWeather(date: Date, locationId: String): List<HourlyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hourlyWeather: HourlyWeather)

    @Query("DELETE FROM HourlyWeather")
    suspend fun deleteAll()
}