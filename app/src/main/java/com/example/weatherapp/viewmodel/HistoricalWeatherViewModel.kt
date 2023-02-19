package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.weatherapp.di.IoDispatcher
import com.example.weatherapp.model.WeatherForecastRepository
import com.example.weatherapp.view.HistoricalWeatherPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoricalWeatherViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(HistoricalWeatherUiState())
    val uiState: LiveData<HistoricalWeatherUiState> get() = _uiState

    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 7)
    ) {
        HistoricalWeatherPagingSource(weatherForecastRepository)
    }.flow.cachedIn(viewModelScope)

    suspend fun moreWeather() {
        withContext(coroutineDispatcher) {
            _uiState.apply {
                postValue(value!!.copy(isRefreshing = true))
                fetchMoreHistoricalWeather()
                postValue(value!!.copy(isRefreshing = false))
            }
        }
    }

    private suspend fun fetchMoreHistoricalWeather() {
        try {
            // TODO: Get location from device
            val latitude = -38.95
            val longitude = -68.07
            val date = Calendar.getInstance().apply { add(Calendar.DATE, -WEATHER_DAYS) }
            val days = -WEATHER_DAYS

            weatherForecastRepository.getHistorical(latitude, longitude, date.time, days).let {
                if (it.isSuccessful) {
                    val moreWeather = it.body()!!.daily.map { daily ->
                        HistoricalWeatherItem(
                            time = parseTime(daily.time),
                            temperatureMax = decimalFormat.format(daily.temperatureMax).plus(DEGREE_CELSIUS),
                            temperatureMin = decimalFormat.format(daily.temperatureMin).plus(DEGREE_CELSIUS),
                            conditionText = daily.conditionText,
                            conditionIcon = daily.conditionIcon
                        )
                    }
                    _uiState.apply {
                        postValue(
                            value!!.copy(
                                historicalWeather = value!!.historicalWeather.plus(moreWeather),
                                isError = false,
                                errorMessage = ""
                            )
                        )
                    }
                } else {
                    // TODO: Handle remote errors
                    it.errorBody()?.let {
                        _uiState.apply {
                            postValue(value!!.copy(isError = true, errorMessage = it.string()))
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, e.localizedMessage, e)
            _uiState.apply {
                postValue(value!!.copy(isError = true, errorMessage = e.localizedMessage!!))
            }
        }
    }

    companion object {
        private val TAG = HistoricalWeatherViewModel::class.java.simpleName

        private const val WEATHER_DAYS = 7
        private const val DEGREE_CELSIUS = "°C"

        @SuppressLint("SimpleDateFormat")
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        @SuppressLint("SimpleDateFormat")
        private val dateFormatUi = SimpleDateFormat("EEEE d")
        private val decimalFormat = DecimalFormat("0.#")

        private fun parseTime(dateString: String): String {
            return try {
                val date = dateFormat.parse(dateString)
                dateFormatUi.format(date)
            } catch (e: Exception) {
                Log.d("DailyWeatherAdapter.parseTime()", e.localizedMessage)
                dateString
            }
        }
    }
}

data class HistoricalWeatherUiState(
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val historicalWeather: List<HistoricalWeatherItem> = listOf()
)

data class HistoricalWeatherItem(
    val time: String,
    val temperatureMax: String,
    val temperatureMin: String,
    val conditionText: String,
    val conditionIcon: Int
)