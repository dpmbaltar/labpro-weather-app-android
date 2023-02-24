package com.example.weatherapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM current_weather WHERE location_id = :locationId ORDER BY time DESC LIMIT 1")
    fun getCurrentWeather(locationId: String): CurrentWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currentWeather: CurrentWeather)

    @Query("DELETE FROM current_weather")
    suspend fun deleteAll()
}