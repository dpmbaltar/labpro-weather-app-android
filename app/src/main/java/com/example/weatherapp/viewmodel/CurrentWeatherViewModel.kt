package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.*
import com.example.weatherapp.model.*
import com.example.weatherapp.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import com.example.weatherapp.util.Result as FlowResult

@SuppressLint("MissingPermission")
@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    locationProvider: FusedLocationProviderClient,
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    data class CurrentWeatherUiModel(
        val time: String,
        val temperature: String,
        val apparentTemperature: String,
        val precipitation: String,
        val humidity: String,
        val windSpeed: String,
        val windDirection: Double,
        val uv: String,
        val isDay: Boolean,
        val conditionText: String,
        val conditionIcon: Int,
        val locationName: String
    )

    private val _uiState = MutableStateFlow<FlowResult<CurrentWeatherResponse>>(FlowResult.Loading)
    private val _location = MutableLiveData<Location>()
    private val location get() = _location.asFlow()

    val error: Flow<Throwable?> = _uiState.filterIsInstance<FlowResult.Error>().map { it.exception }
    val loading: Flow<Boolean> = _uiState.map { it is FlowResult.Loading }
    val currentWeather: Flow<CurrentWeatherUiModel> = _uiState
        .filterIsInstance<FlowResult.Success<CurrentWeatherResponse>>()
        .map { uiState ->
            uiState.data.let { weather ->
                with(weather.current) {
                    CurrentWeatherUiModel(
                        time = time.weekdayDateMonth(),
                        temperature = temperature.degreesCelsius(),
                        apparentTemperature = apparentTemperature.degreesCelsius(),
                        precipitation = precipitation.percent(),
                        humidity = humidity.percent(),
                        windSpeed = windSpeed.kilometersPerHour(),
                        windDirection = windDirection,
                        uv = uv.toString(),
                        isDay = isDay,
                        conditionText = conditionText,
                        conditionIcon = conditionIcon,
                        locationName = "${weather.location.name}, ${weather.location.region}"
                    )
                }
            }
        }

    fun refresh() = viewModelScope.launch {
        location.collect {
            with(it) {
                weatherRepository.getCurrentWeather(latitude, longitude)
                    .asResult()
                    .collect { result ->
                        _uiState.update { result }
                    }
            }
        }
    }

    init {
        locationProvider.lastLocation.addOnSuccessListener {
            _location.postValue(it)
        }
    }
}