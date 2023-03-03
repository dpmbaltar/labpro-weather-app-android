package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.HistoricalWeatherPagingDataAdapter.DailyWeatherViewHolder
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.viewmodel.HistoricalWeatherViewModel.DailyWeatherUiModel

class HistoricalWeatherPagingDataAdapter :
    PagingDataAdapter<DailyWeatherUiModel, DailyWeatherViewHolder>(DiffCallback) {

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

    class DailyWeatherViewHolder(
        private val binding: DailyWeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyWeather: DailyWeatherUiModel?) {
            dailyWeather?.let {
                with(dailyWeather) {
                    binding.date.text = time
                    binding.temperatureMax.text = temperatureMax
                    binding.temperatureMin.text = temperatureMin
                    binding.conditionText.text = conditionText
                    binding.conditionIcon.setImageResource(conditionIcon)
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DailyWeatherUiModel>() {

            override fun areItemsTheSame(
                oldItem: DailyWeatherUiModel,
                newItem: DailyWeatherUiModel
            ): Boolean = oldItem.time == newItem.time

            override fun areContentsTheSame(
                oldItem: DailyWeatherUiModel,
                newItem: DailyWeatherUiModel
            ): Boolean = oldItem == newItem
        }
    }
}