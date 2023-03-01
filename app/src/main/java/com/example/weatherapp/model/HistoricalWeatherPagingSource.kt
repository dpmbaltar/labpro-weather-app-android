package com.example.weatherapp.model

import android.annotation.SuppressLint
import android.location.Location
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.single
import java.util.*
import java.util.Calendar.*
import javax.inject.Inject

const val PAGE_SIZE = 7

@SuppressLint("MissingPermission")
class HistoricalWeatherPagingSource @Inject constructor(
    private val weatherRepository: WeatherForecastRepository,
    locationProvider: FusedLocationProviderClient
) : PagingSource<Int, DailyWeather>() {

    private var _location: Location? = null
    private val location get() = _location!!

    init {
        locationProvider.lastLocation.addOnSuccessListener { _location = it }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DailyWeather> {
        return try {
            val nextPage = params.key ?: 1
            val date = getInstance().apply {
                set(HOUR_OF_DAY, 0)
                set(MINUTE, 0)
                set(SECOND, 0)
                set(MILLISECOND, 0)
                add(DATE, -PAGE_SIZE.times(nextPage))
            }

            val response = weatherRepository.getHistoricalDailyWeather(
                location.latitude,
                location.longitude,
                date,
                -PAGE_SIZE
            ).single()

            LoadResult.Page(
                data = response.daily,
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