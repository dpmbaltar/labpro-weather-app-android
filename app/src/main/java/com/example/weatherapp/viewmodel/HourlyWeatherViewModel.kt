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

@HiltViewModel
class HourlyWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherForecastRepository
) : ViewModel() {

    private sealed interface UiState {

        object Loading : UiState

        data class Success(
            val data: List<HourlyWeather>
        ) : UiState

        data class Error(
            val throwable: Throwable? = null
        ) : UiState
    }

    data class HourlyWeatherUiModel(
        val time: String,
        val temperature: String,
        val precipitation: String,
        val windSpeed: String,
        val conditionText: String,
        val conditionIcon: Int
    )

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    val error: Flow<Throwable?> = _uiState.filterIsInstance<UiState.Error>().map { it.throwable }
    val loading: Flow<Boolean> = _uiState.map { it is UiState.Loading }
    val hourlyWeather: Flow<List<HourlyWeatherUiModel>> = _uiState
        .filterIsInstance<UiState.Success>()
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
            weatherRepository.getHourlyWeather(latitude, longitude, date).asResult()
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