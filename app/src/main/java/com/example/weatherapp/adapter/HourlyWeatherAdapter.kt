package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.HourlyWeatherAdapter.HourlyWeatherViewHolder
import com.example.weatherapp.databinding.HourlyWeatherItemBinding
import com.example.weatherapp.viewmodel.HourlyWeatherViewModel.HourlyWeatherUiModel

class HourlyWeatherAdapter :
    ListAdapter<HourlyWeatherUiModel, HourlyWeatherViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        return HourlyWeatherViewHolder(
            HourlyWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HourlyWeatherViewHolder(
        private val binding: HourlyWeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hourlyWeather: HourlyWeatherUiModel) {
            with(hourlyWeather) {
                binding.time.text = time
                binding.temperature.text = temperature
                binding.precipitation.text = precipitation
                binding.windSpeed.text = windSpeed
                binding.conditionIcon.setImageResource(conditionIcon)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<HourlyWeatherUiModel>() {

            override fun areItemsTheSame(
                oldItem: HourlyWeatherUiModel,
                newItem: HourlyWeatherUiModel
            ): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(
                oldItem: HourlyWeatherUiModel,
                newItem: HourlyWeatherUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}