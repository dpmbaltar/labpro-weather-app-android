package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.*
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class DailyWeatherViewModel @Inject constructor(
    locationProvider: FusedLocationProviderClient,
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    sealed interface UiState {

        object Loading : UiState

        data class Success(
            val data: DailyWeatherResponse
        ) : UiState

        data class Error(
            val throwable: Throwable? = null
        ) : UiState
    }

    private val _location = MutableLiveData<Location>()
    private val location: Flow<Location> get() = _location.asFlow()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        locationProvider.lastLocation.addOnSuccessListener { _location.postValue(it) }
    }

    fun refresh() {
        viewModelScope.launch {
            location.collectLatest { location ->
                weatherRepository
                    .getDailyWeather(location.latitude, location.longitude).asResult()
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
}