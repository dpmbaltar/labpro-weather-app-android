package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.DailyWeatherAdapter.DailyWeatherViewHolder
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.viewmodel.DailyWeatherViewModel.DailyWeatherUiModel

class DailyWeatherAdapter(
    private val onItemClicked: (DailyWeatherUiModel, Int) -> Unit
) : ListAdapter<DailyWeatherUiModel, DailyWeatherViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val viewHolder = DailyWeatherViewHolder(
            DailyWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            onItemClicked(getItem(position), position)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DailyWeatherViewHolder(
        private var binding: DailyWeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyWeather: DailyWeatherUiModel) {
            with(dailyWeather) {
                binding.date.text = time
                binding.temperatureMax.text = temperatureMax
                binding.temperatureMin.text = temperatureMin
                binding.conditionText.text = conditionText
                binding.conditionIcon.setImageResource(conditionIcon)
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