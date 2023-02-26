package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.model.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
        locationDao: WeatherLocationDao,
        currentWeatherDao: CurrentWeatherDao,
        dailyWeatherDao: DailyWeatherDao,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): WeatherForecastLocalDataSource =
        WeatherForecastLocalDataSource.getInstance(
            locationDao,
            currentWeatherDao,
            dailyWeatherDao,
            coroutineDispatcher
        )

    @Singleton
    @Provides
    fun providesWeatherForecastRemoteDataSource(
        weatherService: WeatherForecastService
    ): WeatherForecastRemoteDataSource =
        WeatherForecastRemoteDataSource.getInstance(weatherService)

    @Singleton
    @Provides
    fun providesWeatherForecastRepository(
        weatherService: WeatherForecastService,
        weatherLocalDataSource: WeatherForecastLocalDataSource,
        weatherRemoteDataSource: WeatherForecastRemoteDataSource
    ): WeatherForecastRepository =
        WeatherForecastRepository.getInstance(
            weatherService,
            weatherLocalDataSource,
            weatherRemoteDataSource
        )
}