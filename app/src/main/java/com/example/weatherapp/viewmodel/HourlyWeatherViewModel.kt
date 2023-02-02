package com.example.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.model.WeatherLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HourlyWeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val location = MutableLiveData<WeatherLocation>()
    val hourly = MutableLiveData<List<HourlyWeather>>()
    val error = MutableLiveData<String>()

    fun fetchHourlyWeather(lat: Double, lon: Double, date: Calendar) {
        GlobalScope.launch(Dispatchers.IO) { loadHourlyWeather(lat, lon, date) }
    }

    private suspend fun loadHourlyWeather(lat: Double, lon: Double, date: Calendar) {
        try {
            val response = weatherForecastRepository.getHourly(lat, lon, date)
            if (response.isSuccessful) {
                response.body()?.let {
                    val hourlyValues = it.hourly[0]
                    val hourlyItems = arrayListOf<HourlyWeather>()

                    for (i in 0..23) {
                        hourlyItems.add(
                            HourlyWeather(
                                time = hourlyValues.time[i],
                                temperature = hourlyValues.temperature[i],
                                precipitation = hourlyValues.precipitation[i],
                                windSpeed = hourlyValues.windSpeed[i],
                                conditionIcon = hourlyValues.conditionIcon[i]
                            )
                        )
                    }

                    hourly.postValue(hourlyItems)
                    location.postValue(it.location)
                }
            } else {
                response.errorBody()?.let {
                    error.postValue(it.string()) // TODO: Handle remote errors
                }
            }
        } catch (e: IOException) {
            error.postValue(e.localizedMessage) // TODO: Handle exceptions
        }
    }
}