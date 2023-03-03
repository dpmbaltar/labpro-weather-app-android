package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.weatherapp.model.HistoricalWeatherPagingSource
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.util.PAGE_SIZE
import com.example.weatherapp.util.degreesCelsius
import com.example.weatherapp.util.weatherIcon
import com.example.weatherapp.util.weekdayDateMonth
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HistoricalWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherForecastRepository,
    private val locationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    data class DailyWeatherUiModel(
        val time: String,
        val temperatureMax: String,
        val temperatureMin: String,
        val conditionText: String,
        val conditionIcon: Int
    )

    val daily: Flow<PagingData<DailyWeatherUiModel>> = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        HistoricalWeatherPagingSource(weatherRepository, locationProviderClient)
    }.flow.map { pagingData ->
        pagingData.map { dailyWeather ->
            with(dailyWeather) {
                DailyWeatherUiModel(
                    time = time.weekdayDateMonth(),
                    temperatureMax = temperatureMax.degreesCelsius(),
                    temperatureMin = temperatureMin.degreesCelsius(),
                    conditionIcon = conditionIcon.weatherIcon(),
                    conditionText = conditionText
                )
            }
        }
    }.cachedIn(viewModelScope)
}