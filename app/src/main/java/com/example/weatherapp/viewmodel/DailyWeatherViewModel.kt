package com.example.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.model.WeatherLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DailyWeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val location = MutableLiveData<WeatherLocation>()
    val daily = MutableLiveData<List<DailyWeather>>()
    val error = MutableLiveData<String>()

    suspend fun loadDailyWeather(latitude: Double, longitude: Double) {
        try {
            val dailyWeatherResponse = weatherForecastRepository.getDaily(latitude, longitude)
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