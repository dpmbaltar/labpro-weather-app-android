package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.model.WeatherDatabase
import com.example.weatherapp.model.WeatherForecastLocalDataSource
import com.example.weatherapp.model.WeatherForecastRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun providesWeatherForecastLocalDataSource(
        weatherDatabase: WeatherDatabase
    ): WeatherForecastLocalDataSource =
        WeatherForecastLocalDataSource.getInstance(weatherDatabase)

    @Singleton
    @Provides
    fun providesWeatherForecastRepository(
        weatherService: WeatherForecastService,
        weatherLocalDataSource: WeatherForecastLocalDataSource
    ): WeatherForecastRepository =
        WeatherForecastRepository.getInstance(weatherService, weatherLocalDataSource)
}