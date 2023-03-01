package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*
import java.util.Calendar.DATE

@Dao
interface DailyWeatherDao {

    @Query(
        "SELECT * FROM DailyWeather " +
        "WHERE locationId = :locationId AND time > :after AND timestamp > 0 " +
        "ORDER BY time ASC, timestamp DESC LIMIT :limit"
    )
    fun getDailyWeather(
        locationId: String,
        after: Date = Calendar.getInstance().apply { add(DATE, -1) }.time,
        limit: Int = 7
    ): List<DailyWeather>

    @Query(
        "SELECT * FROM DailyWeather " +
        "WHERE locationId = :locationId AND time < :before AND timestamp = 0 " +
        "ORDER BY time DESC LIMIT :limit"
    )
    fun getHistoricalDailyWeather(
        locationId: String,
        before: Calendar,
        limit: Int = 7
    ): List<DailyWeather>

    @Query(
        "DELETE FROM DailyWeather " +
        "WHERE locationId = :locationId AND timestamp BETWEEN 1 AND :timestamp - 1"
    )
    suspend fun deleteOld(locationId: String, timestamp: Calendar)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyWeather: DailyWeather)
}