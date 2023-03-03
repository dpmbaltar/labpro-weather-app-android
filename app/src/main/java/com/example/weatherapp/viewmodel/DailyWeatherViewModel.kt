package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.*
import com.example.weatherapp.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import com.example.weatherapp.util.Result as FlowResult

@SuppressLint("MissingPermission")
@HiltViewModel
class DailyWeatherViewModel @Inject constructor(
    locationProvider: FusedLocationProviderClient,
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    data class DailyWeatherUiModel(
        val time: String,
        val temperatureMax: String,
        val temperatureMin: String,
        val sunrise: String,
        val sunset: String,
        val conditionText: String,
        val conditionIcon: Int,
        val date: String,
        val latitude: Double,
        val longitude: Double
    )

    private val _uiState = MutableStateFlow<FlowResult<DailyWeatherResponse>>(FlowResult.Loading)
    private val _location = MutableLiveData<Location>()
    private val location: Flow<Location> get() = _location.asFlow()

    val error: Flow<Throwable?> = _uiState.filterIsInstance<FlowResult.Error>().map { it.exception }
    val loading: Flow<Boolean> = _uiState.map { it is FlowResult.Loading }
    val dailyWeather: Flow<List<DailyWeatherUiModel>> = _uiState
        .filterIsInstance<FlowResult.Success<DailyWeatherResponse>>()
        .map { uiState ->
            uiState.data.daily.mapIndexed { _, dailyWeather ->
                with(dailyWeather) {
                    DailyWeatherUiModel(
                        time = time.weekdayDate(),
                        temperatureMax = temperatureMax.degreesCelsius(),
                        temperatureMin = temperatureMin.degreesCelsius(),
                        sunrise = sunrise,
                        sunset = sunset,
                        conditionText = conditionText,
                        conditionIcon = conditionIcon.weatherIcon(),
                        date = time.isoDate(),
                        latitude = uiState.data.location.latitude,
                        longitude = uiState.data.location.longitude
                    )
                }
            }
        }

    fun refresh() = viewModelScope.launch {
        location.collect {
            with(it) {
                weatherRepository.getDailyWeather(latitude, longitude)
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