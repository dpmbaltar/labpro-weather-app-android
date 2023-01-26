package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.model.Condition
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.WeatherForecastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherCurrentViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    private val conditions = HashMap<Int, Condition>()
    val error = MutableLiveData<String>()
    val location = MutableLiveData<Location>()
    val current = MutableLiveData<Current>()
    val conditionText: LiveData<String> = Transformations.map(current) {
        it?.let {
            conditions[it.weatherCode]?.let { condition ->
                if (it.isDay) condition.day else condition.night
            }
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadConditions()
            loadWeatherCurrent()
        }
    }

    private suspend fun loadConditions() {
        try {
            val conditionsList = weatherForecastRepository.getConditions()
            if (conditionsList.isSuccessful) {
                conditionsList.body()?.forEach { condition ->
                    conditions[condition.code] = condition
                }
            }
        } catch (e: IOException) {
            error.postValue(e.localizedMessage)
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