package com.example.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.model.WeatherLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DailyWeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val location = MutableLiveData<WeatherLocation>()
    val daily = MutableLiveData<List<DailyWeather>>()
    val error = MutableLiveData<String>()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadDailyWeather()
        }
    }

    private suspend fun loadDailyWeather() {
        try {
            // TODO: Get location from device
            val dailyWeatherResponse = weatherForecastRepository.getDaily(-38.95, -68.07)
            if (dailyWeatherResponse.isSuccessful) {
                dailyWeatherResponse.body()?.let {
                    location.postValue(it.location)
                    daily.postValue(it.daily)
                }
            } else {
                dailyWeatherResponse.errorBody()?.let {
                    error.postValue(it.string()) // TODO: Handle remote errors
                }
            }
        } catch (e: IOException) {
            error.postValue(e.localizedMessage)
        }
    }

}