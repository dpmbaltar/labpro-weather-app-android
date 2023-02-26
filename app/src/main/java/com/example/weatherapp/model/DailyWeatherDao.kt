package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface DailyWeatherDao {

    @Query("SELECT * FROM DailyWeather WHERE locationId = :locationId")
    fun getDailyWeather(locationId: String): List<DailyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyWeather: DailyWeather)

    @Query("DELETE FROM DailyWeather WHERE locationId = :locationId AND timestamp < :timestamp")
    suspend fun deleteOld(locationId: String, timestamp: Calendar)
}