package com.example.weatherapp.model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class WeatherForecastProvider(application: Application) {

    companion object Api {
        private fun builder(): Uri.Builder {
            return Uri.Builder()
                .scheme("http")
                .encodedAuthority("192.168.0.239:9000")
                .appendPath("api")
                .appendPath("weather")
        }
    }

    private val requeue = Volley.newRequestQueue(application)
    private val error = MutableLiveData<JSONObject>()

    fun requestCurrentWeather(responseBlock: (CurrentWeatherModel) -> (Unit)) {
        val url = builder()
            .appendPath("current")
            .appendQueryParameter("latitude", (-38.95).toString())
            .appendQueryParameter("longitude", (-68.07).toString())
            .build().toString()

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                val currentWeather = it.getJSONObject("current")
                responseBlock(CurrentWeatherModel(
                    time = currentWeather.getString("time"),
                    temperature = currentWeather.getDouble("temperature"),
                    feelsLike = currentWeather.getDouble("feelslike"),
                    humidity = currentWeather.getDouble("humidity"),
                    windSpeed = currentWeather.getDouble("windspeed"),
                    windDirection = currentWeather.getDouble("winddirection"),
                    weatherCode = currentWeather.getInt("weathercode"),
                    uv = currentWeather.getInt("uv"),
                    isDay = currentWeather.getInt("is_day") == 1
                ))
            },
            {
                when (it.networkResponse) {
                    null -> return@JsonObjectRequest
                    else -> error.value = parseErrorResponse(it.networkResponse)
                }
            }
        )

        requeue.add(jsonRequest)
    }

    fun requestConditions(responseBlock: (HashMap<Int, ConditionModel>) -> (Unit)) {
        val url = builder().appendPath("conditions").build().toString()
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val conditions = HashMap<Int, ConditionModel>()
                for (i in 0 until response.length()) {
                    val condition = response.getJSONObject(i)
                    val code = condition.getInt("code")
                    conditions[code] = ConditionModel(
                        code = code,
                        day = condition.getString("day"),
                        night = condition.getString("night"),
                        icon = condition.getInt("icon")
                    )
                }

                responseBlock(conditions)
            },
            {
                when (it.networkResponse) {
                    null -> return@JsonArrayRequest
                    else -> error.value = parseErrorResponse(it.networkResponse)
                }
            }
        )

        requeue.add(jsonRequest)
    }

    private fun parseErrorResponse(response: NetworkResponse): JSONObject? {
        val charset = Charset.forName(HttpHeaderParser.parseCharset(response.headers))
        val data = String(response.data, charset)

        return try {
            JSONObject(data)
        } catch (e: JSONException) {
            null
        }
    }

}