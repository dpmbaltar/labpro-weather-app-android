package com.example.weatherapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.util.DATABASE_NAME

@Database(
    entities = [
        WeatherLocation::class,
        CurrentWeather::class,
        DailyWeather::class,
        HourlyWeather::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherLocationDao(): WeatherLocationDao
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun dailyWeatherDao(): DailyWeatherDao
    abstract fun hourlyWeatherDao(): HourlyWeatherDao

    companion object {

        @Volatile
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
        }
    }
}