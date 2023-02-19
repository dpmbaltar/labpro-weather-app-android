package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.model.HistoricalWeatherPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoricalWeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val weatherFlow = Pager(PagingConfig(pageSize = 7)) {
        HistoricalWeatherPagingSource(weatherForecastRepository)
    }.flow.cachedIn(viewModelScope)
}