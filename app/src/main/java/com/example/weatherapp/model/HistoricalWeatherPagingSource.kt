package com.example.weatherapp.model

import android.annotation.SuppressLint
import android.location.Location
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.*
import javax.inject.Inject

const val PAGE_SIZE = 7

@SuppressLint("MissingPermission")
class HistoricalWeatherPagingSource @Inject constructor(
    private val weatherRepository: WeatherForecastRepository,
    locationProvider: FusedLocationProviderClient
) : PagingSource<Int, DailyWeather>() {

    private val today: Calendar = Calendar.getInstance()
    private var _location: Location? = null
    private val location get() = _location!!

    init {
        locationProvider.lastLocation.addOnSuccessListener { _location = it }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DailyWeather> {
        return try {
            val nextPage = params.key ?: 1
            val date = today.apply { add(Calendar.DATE, -PAGE_SIZE.times(nextPage)) }
            val response = weatherRepository.getHistorical(
                location.latitude,
                location.longitude,
                date.time,
                -PAGE_SIZE
            )

            LoadResult.Page(
                data = response.body()!!.daily,
                prevKey = null,
                nextKey = nextPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DailyWeather>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}