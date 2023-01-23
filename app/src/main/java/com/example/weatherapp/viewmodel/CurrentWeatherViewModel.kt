package com.example.weatherapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.model.ConditionModel
import com.example.weatherapp.model.CurrentWeatherModel
import com.example.weatherapp.model.WeatherForecastProvider
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherForecastProvider = WeatherForecastProvider(application)
    private val conditions = HashMap<Int, ConditionModel>()

    val error = MutableLiveData<JSONObject>()
    val weather = MutableLiveData<CurrentWeatherModel>()

    fun getCurrentWeather() {
        weatherForecastProvider.requestCurrentWeather() {
            weather.postValue(it)
        }
    }

    fun getConditions() {
        weatherForecastProvider.requestConditions() {
            conditions.putAll(it)
        }
    }

    fun conditionText(weatherCode: Int, isDay: Boolean = true): String {
        return if (isDay)
            conditions[weatherCode]!!.day
        else
            conditions[weatherCode]!!.night
    }

}