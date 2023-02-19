package com.example.weatherapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.databinding.LoadingItemBinding
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.util.WeatherIcon
import com.example.weatherapp.viewmodel.HistoricalWeatherItem

val USER_COMPARATOR = object : DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean =
        // User ID serves as unique ID
        oldItem.time == newItem.time

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean =
        // Compare full contents (note: Java users should call .equals())
        oldItem == newItem
}

class DailyWeatherViewHolder(
    private var binding: DailyWeatherItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: DailyWeather?) {
        weather?.let {
            with(binding) {
                date.text = weather.time
                temperatureMax.text = weather.temperatureMax.toString()
                temperatureMin.text = weather.temperatureMin.toString()
                conditionText.text = weather.conditionText
                conditionIcon.setImageResource(WeatherIcon.getDrawableId(weather.conditionIcon))
            }
        }
    }
}

private class LoadingViewHolder(
    private var binding: LoadingItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    var progressBar: ProgressBar = binding.progressBar
}

class HistoricalWeatherPagingDataAdapter(

) : PagingDataAdapter<DailyWeather, DailyWeatherViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        return DailyWeatherViewHolder(
            DailyWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val repoItem = getItem(position)
        // Note that item may be null, ViewHolder must support binding null item as placeholder
        holder.bind(repoItem)
    }
}