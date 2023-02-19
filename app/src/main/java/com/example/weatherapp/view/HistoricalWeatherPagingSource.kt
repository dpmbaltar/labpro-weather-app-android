package com.example.weatherapp.view

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.model.WeatherForecastRepository
import java.util.Calendar

class HistoricalWeatherPagingSource(
    private val weatherRepository: WeatherForecastRepository
) : PagingSource<Int, DailyWeather>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DailyWeather> {
        return try {
            val nextPage = params.key ?: 1
            val latitude = -38.95 // TODO: get user location
            val longitude = -68.07
            val days = -7
            val date = Calendar.getInstance().apply { add(Calendar.DATE, days.times(nextPage)) }
            val response = weatherRepository.getHistorical(latitude, longitude, date.time, days)

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