package com.example.weatherapp.view

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.model.WeatherForecastRepository
import java.text.SimpleDateFormat
import java.util.*

class HistoricalWeatherPagingSource(
    private val weatherForecastRepository: WeatherForecastRepository
) : PagingSource<Int, DailyWeather>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, DailyWeather> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val latitude = -38.95
            val longitude = -68.07
            val days = -7
            val date = Calendar.getInstance().apply { add(Calendar.DATE, days.times(nextPageNumber)) }
            val response = weatherForecastRepository.getHistorical(latitude, longitude, date.time, days)
            return LoadResult.Page(
                data = response.body()!!.daily,
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber.plus(1)
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DailyWeather>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}