package com.example.weatherapp.viewmodel

import androidx.lifecycle.*
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.WeatherLocation
import com.example.weatherapp.model.WeatherForecastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val location = MutableLiveData<WeatherLocation>()
    val current = MutableLiveData<CurrentWeather>()
    val error = MutableLiveData<String>()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadWeatherCurrent()
        }
    }

    private suspend fun loadWeatherCurrent() {
        try {
            // TODO: Get location from device
            val weatherCurrentResponse = weatherForecastRepository.getCurrent(-38.95, -68.07)
            if (weatherCurrentResponse.isSuccessful) {
                weatherCurrentResponse.body()?.let {
                    location.postValue(it.location)
                    current.postValue(it.current)
                }
            } else {
                weatherCurrentResponse.errorBody()?.let {
                    error.postValue(it.string()) // TODO: Handle remote errors
                }
            }
        } catch (e: IOException) {
            error.postValue(e.localizedMessage)
        }
    }

}