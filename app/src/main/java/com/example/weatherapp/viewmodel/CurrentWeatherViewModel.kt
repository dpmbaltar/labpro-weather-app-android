package com.example.weatherapp.viewmodel

import androidx.lifecycle.*
import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.WeatherLocation
import com.example.weatherapp.model.WeatherForecastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val refreshing = MutableLiveData<Boolean>().apply { value = true }
    val location = MutableLiveData<WeatherLocation>()
    val current = MutableLiveData<CurrentWeather>()
    val error = MutableLiveData<String>()

    suspend fun refresh() {
        withContext(coroutineDispatcher) {
            refreshing.postValue(true)
            fetchCurrentWeather()
            refreshing.postValue(false)
        }
    }

    private suspend fun fetchCurrentWeather() {
        try {
            // TODO: Get location from device
            val response = weatherForecastRepository.getCurrent(-38.95, -68.07)
            if (response.isSuccessful) {
                response.body()?.let {
                    location.postValue(it.location)
                    current.postValue(it.current)
                }
            } else {
                response.errorBody()?.let {
                    error.postValue(it.string()) // TODO: Handle remote errors
                }
            }
        } catch (e: IOException) {
            error.postValue(e.localizedMessage)
        }
    }
}