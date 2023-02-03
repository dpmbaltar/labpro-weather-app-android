package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.model.CurrentWeatherResponse
import com.example.weatherapp.model.WeatherForecastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    data class CurrentWeatherUiState(
        val isRefreshing: Boolean = false,
        val isLoaded: Boolean = false,
        val isFailed: Boolean = false,
        val lastError: String = ""
    )

    data class CurrentWeatherItemUiState(
        val locationName: String,
        val currentTime: String,
        val temperature: String,
        val humidity: String,
        val windSpeed: String,
        val uv: String,
        val isDay: Boolean,
        val conditionText: String,
        val conditionIcon: Int
    )

    private val _uiState = MutableLiveData(CurrentWeatherUiState())
    val uiState: LiveData<CurrentWeatherUiState> get() = _uiState

    private val _currentWeather = MutableLiveData<CurrentWeatherResponse>()
    val currentWeather: LiveData<CurrentWeatherItemUiState> = Transformations.map(_currentWeather) {
        with(it) {
            CurrentWeatherItemUiState(
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
    }

    suspend fun refresh() {
        withContext(coroutineDispatcher) {
            _uiState.apply {
                postValue(value!!.copy(isRefreshing = true))
                fetchCurrentWeather()
                postValue(value!!.copy(isRefreshing = false))
            }
        }
    }

    private suspend fun fetchCurrentWeather() {
        try {
            // TODO: Get location from device
            val response = weatherForecastRepository.getCurrent(-38.95, -68.07)
            if (response.isSuccessful) {
                _currentWeather.postValue(response.body())
            } else {
                // TODO: Handle remote errors
                response.errorBody()?.let {
                    _uiState.apply {
                        postValue(value!!.copy(isFailed = true, lastError = it.string()))
                    }
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, e.localizedMessage, e)
            _uiState.apply {
                postValue(value!!.copy(isFailed = true, lastError = e.localizedMessage!!))
            }
        }
    }

    companion object {
        private val TAG = CurrentWeatherViewModel::class.java.simpleName

        private const val DEGREE_CELSIUS = "°C"
        private const val KM_H = " km/h"
        private const val PERCENT = "%"

        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("EEEE, d MMMM")
        private val decimalFormat = DecimalFormat("0.#")
    }
}