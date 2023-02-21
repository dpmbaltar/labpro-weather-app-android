package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.model.CurrentWeatherDao
import com.example.weatherapp.model.WeatherDatabase
import com.example.weatherapp.model.WeatherLocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase =
        WeatherDatabase.getInstance(context)

    @Provides
    fun providesWeatherLocationDao(weatherDatabase: WeatherDatabase): WeatherLocationDao =
        weatherDatabase.weatherLocationDao()

    @Provides
    fun providesCurrentWeatherDao(weatherDatabase: WeatherDatabase): CurrentWeatherDao =
        weatherDatabase.currentWeatherDao()
}