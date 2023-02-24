package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.model.*
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    locationProviderClient: FusedLocationProviderClient,
    private val weatherRepository: WeatherForecastRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
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

    /*data class CurrentWeatherUiState(
        val isRefreshing: Boolean = false,
        val isLoaded: Boolean = false,
        val isError: Boolean = false,
        val lastError: String = ""
    )

    data class CurrentWeatherResultUiState(
        val locationName: String,
        val currentTime: String,
        val temperature: String,
        val humidity: String,
        val windSpeed: String,
        val uv: String,
        val isDay: Boolean,
        val conditionText: String,
        val conditionIcon: Int
    )*/

    private val _location = MutableLiveData<Location>()
    private val location get() = _location.asFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    //private val _currentWeather: LiveData<CurrentWeatherResponse> = refresh()
    /*val currentWeather: LiveData<CurrentWeatherResultUiState> = Transformations.map(_currentWeather) {
        with(it) {
            CurrentWeatherResultUiState(
                locationName = "${location.name}, ${location.region}",
                currentTime = dateFormat.format(current.time),
                temperature = decimalFormat.format(current.temperature).plus(DEGREE_CELSIUS),
                humidity = decimalFormat.format(current.humidity).plus(PERCENT),
                windSpeed = decimalFormat.format(current.windSpeed).plus(KM_H),
                uv = decimalFormat.format(current.uv),
                isDay = current.isDay,
                conditionText = current.conditionText,
                conditionIcon = current.conditionIcon
            )
        }
    }*/

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

    /*suspend fun refresh() = liveData {
        _uiState.collect { it.copy() }
        location.collectLatest { location ->
            weatherRepository.getCurrentWeather(location.latitude, location.longitude).collect {
                emit(it)
            }
        }
    }{
        withContext(coroutineDispatcher) {
            _uiState.apply {
                postValue(value!!.copy(isRefreshing = true))
                //fetchCurrentWeather(latitude, longitude)
                postValue(value!!.copy(isRefreshing = false))
            }
        }
    }*/

    /*private suspend fun fetchCurrentWeather(latitude: Double, longitude: Double) {
        try {
            val response = weatherRepository.getCurrent(latitude, longitude)
            if (response.isSuccessful) {
                //_currentWeather.postValue(response.body())
            } else {
                // TODO: Handle remote errors
                response.errorBody()?.let {
                    _uiState.apply {
                        postValue(value!!.copy(isError = true, lastError = it.string()))
                    }
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, e.localizedMessage, e)
            _uiState.apply {
                postValue(value!!.copy(isError = true, lastError = e.localizedMessage!!))
            }
        }
    }*/

    init {
        locationProviderClient.lastLocation.addOnSuccessListener { _location.postValue(it) }
    }

    companion object {
        private val TAG = CurrentWeatherViewModel::class.java.simpleName

        private const val DEGREE_CELSIUS = "°C"
        private const val KM_H = " km/h"
        private const val PERCENT = "%"

        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("EEEE, d MMM")
        private val decimalFormat = DecimalFormat("0.#")
    }
}