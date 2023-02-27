package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.*
import com.example.weatherapp.model.*
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    locationProvider: FusedLocationProviderClient,
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    sealed interface UiState {

        object Loading : UiState

        data class Success(
            val data: CurrentWeatherResponse
        ) : UiState

        data class Error(
            val throwable: Throwable? = null
        ) : UiState
    }

    data class CurrentWeatherUiState (
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

    private val _location = MutableLiveData<Location>()
    private val location get() = _location.asFlow()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState
    val isRefreshing: Flow<Boolean> = _uiState.map { it is UiState.Loading }
    val error: Flow<Throwable?> = _uiState.filterIsInstance<UiState.Error>().map { it.throwable }
    val currentWeather: Flow<CurrentWeatherUiState> = _uiState.filterIsInstance<UiState.Success>()
        .map { uiState ->
            uiState.data.let { weather ->
                with(weather.current) {
                    CurrentWeatherUiState(
                        time = dateFormat.format(time),
                        temperature = decimalFormat.format(temperature).plus(DEG_C),
                        apparentTemperature = decimalFormat.format(apparentTemperature).plus(DEG_C),
                        precipitation = decimalFormat.format(precipitation).plus(PERCENT),
                        humidity = decimalFormat.format(humidity).plus(PERCENT),
                        windSpeed = decimalFormat.format(windSpeed).plus(KM_H),
                        windDirection = windDirection,
                        uv = decimalFormat.format(uv),
                        isDay = isDay,
                        conditionText = conditionText,
                        conditionIcon = conditionIcon,
                        locationName = "${weather.location.name}, ${weather.location.region}"
                    )
                }
            }
        }

    init {
        locationProvider.lastLocation.addOnSuccessListener { _location.postValue(it) }
    }

    fun refresh() {
        viewModelScope.launch {
            location.collectLatest {
                weatherRepository
                    .getCurrentWeather(it.latitude, it.longitude).asResult()
                    .collect { result ->
                        _uiState.update {
                            when (result) {
                                is Result.Loading -> UiState.Loading
                                is Result.Success -> UiState.Success(result.data)
                                is Result.Error -> UiState.Error(result.exception)
                            }
                        }
                }
            }
        }
    }

    companion object {

        private const val DEG_C = "°C"
        private const val KM_H = " km/h"
        private const val PERCENT = "%"

        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("EEEE, d MMM")
        private val decimalFormat = DecimalFormat("0.#")
    }
}