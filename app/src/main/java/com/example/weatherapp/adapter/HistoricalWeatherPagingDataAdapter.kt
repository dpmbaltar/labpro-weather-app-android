package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.util.WeatherIcon

val USER_COMPARATOR = object : DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean =
        oldItem.time.time == newItem.time.time

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean =
        oldItem == newItem
}

class DailyWeatherViewHolder(
    private var binding: DailyWeatherItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: DailyWeather?) {
        weather?.let {
            with(binding) {
                date.text = weather.time.toString()
                temperatureMax.text = weather.temperatureMax.toString()
                temperatureMin.text = weather.temperatureMin.toString()
                conditionText.text = weather.conditionText
                conditionIcon.setImageResource(WeatherIcon.getDrawableId(weather.conditionIcon))
            }
        }
    }
}

class HistoricalWeatherPagingDataAdapter :
    PagingDataAdapter<DailyWeather, DailyWeatherViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        return DailyWeatherViewHolder(
            DailyWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        // Note that item may be null, ViewHolder must support binding null item as placeholder
        val repoItem = getItem(position)
        holder.bind(repoItem)
    }
}