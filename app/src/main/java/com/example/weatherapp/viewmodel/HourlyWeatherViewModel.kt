package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import com.example.weatherapp.util.Result as FlowResult

@HiltViewModel
class HourlyWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    data class HourlyWeatherUiModel(
        val time: String,
        val temperature: String,
        val precipitation: String,
        val windSpeed: String,
        val conditionText: String,
        val conditionIcon: Int
    )

    private val _uiState = MutableStateFlow<FlowResult<List<HourlyWeather>>>(FlowResult.Loading)

    val error: Flow<Throwable?> = _uiState.filterIsInstance<FlowResult.Error>().map { it.exception }
    val loading: Flow<Boolean> = _uiState.map { it is FlowResult.Loading }
    val hourlyWeather: Flow<List<HourlyWeatherUiModel>> = _uiState
        .filterIsInstance<FlowResult.Success<List<HourlyWeather>>>()
        .map { uiState ->
            uiState.data.mapIndexed { _, hourlyWeather ->
                with(hourlyWeather) {
                    HourlyWeatherUiModel(
                        time = time.hourMinutes(),
                        temperature = temperature.degreesCelsius(),
                        precipitation = precipitation.percent(),
                        windSpeed = windSpeed.kilometersPerHour(),
                        conditionIcon = conditionIcon.weatherIcon(),
                        conditionText = conditionText
                    )
                }
            }
        }

    fun loadHourlyWeather(latitude: Double, longitude: Double, date: Calendar) =
        viewModelScope.launch {
            weatherRepository.getHourlyWeather(latitude, longitude, date)
                .asResult()
                .collect { result ->
                    _uiState.update { result }
                }
        }
}