package com.example.weatherapp.model

import javax.inject.Inject

class WeatherForecastLocalDataSource @Inject constructor(
    private val weatherDatabase: WeatherDatabase
) {

    // TODO: local data source

    companion object {

        @Volatile
        private var instance: WeatherForecastLocalDataSource? = null

        fun getInstance(weatherDatabase: WeatherDatabase) =
            instance ?: synchronized(this) {
                instance ?: WeatherForecastLocalDataSource(weatherDatabase).also { instance = it }
            }
    }
}