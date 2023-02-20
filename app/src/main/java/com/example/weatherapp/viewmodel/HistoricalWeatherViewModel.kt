package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.weatherapp.model.HistoricalWeatherPagingSource
import com.example.weatherapp.model.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoricalWeatherViewModel @Inject constructor(
    private val pagingSource: HistoricalWeatherPagingSource
) : ViewModel() {

    val daily = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        pagingSource
    }.flow.cachedIn(viewModelScope)
}