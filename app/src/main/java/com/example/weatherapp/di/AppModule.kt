package com.example.weatherapp.di

import android.app.Application
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesFusedLocationProviderClient(app: Application)
        = LocationServices.getFusedLocationProviderClient(app)
}