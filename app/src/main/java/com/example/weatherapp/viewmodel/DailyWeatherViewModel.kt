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

@SuppressLint("MissingPermission")
@HiltViewModel
class DailyWeatherViewModel @Inject constructor(
    locationProvider: FusedLocationProviderClient,
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    private sealed interface UiState {

        object Loading : UiState

        data class Success(
            val data: DailyWeatherResponse
        ) : UiState

        data class Error(
            val throwable: Throwable? = null
        ) : UiState
    }

    data class DailyWeatherUiState(
        val time: String,
        val temperatureMax: String,
        val temperatureMin: String,
        val conditionText: String,
        val conditionIcon: Int
    )

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    private val _location = MutableLiveData<Location>()
    private val location: Flow<Location> get() = _location.asFlow()

    val error: Flow<Throwable?> = _uiState.filterIsInstance<UiState.Error>().map { it.throwable }
    val loading: Flow<Boolean> = _uiState.map { it is UiState.Loading }
    val dailyWeather: Flow<List<DailyWeatherUiState>> = _uiState.filterIsInstance<UiState.Success>()
        .map { uiState ->
            uiState.data.daily.mapIndexed { _, dailyWeather ->
                with(dailyWeather) {
                    DailyWeatherUiState(
                        time = time.weekdayDate(),
                        temperatureMax = temperatureMax.degreesCelsius(),
                        temperatureMin = temperatureMin.degreesCelsius(),
                        conditionText = conditionText,
                        conditionIcon = conditionIcon
                    )
                }
            }
        }

    fun refresh() = viewModelScope.launch {
        location.collect {
            with(it) {
                weatherRepository.getDailyWeather(latitude, longitude).asResult()
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

    init {
        locationProvider.lastLocation.addOnSuccessListener {
            _location.postValue(it)
        }
    }
}