package com.example.weatherapp.di

import com.example.weatherapp.api.WeatherForecastService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesWeatherForecastService(): WeatherForecastService {
        return WeatherForecastService.create()
    }
}