package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.weatherapp.model.HistoricalWeatherPagingSource
import com.example.weatherapp.model.PAGE_SIZE
import com.example.weatherapp.model.WeatherForecastRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoricalWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherForecastRepository,
    private val locationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    val daily = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        HistoricalWeatherPagingSource(weatherRepository, locationProviderClient)
    }.flow.cachedIn(viewModelScope)
}