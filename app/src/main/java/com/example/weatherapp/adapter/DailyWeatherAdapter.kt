package com.example.weatherapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.util.WeatherIcon
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class DailyWeatherAdapter(
    private val onItemClicked: (DailyWeather, Int) -> Unit
) : ListAdapter<DailyWeather, DailyWeatherAdapter.DailyWeatherViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DailyWeather>() {

            override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
                return oldItem.time.time == newItem.time.time
            }

            override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val viewHolder = DailyWeatherViewHolder(
            DailyWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
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

        fun bind(dailyWeather: DailyWeather) {
            with(dailyWeather) {
                binding.date.text = dateFormat.format(time)
                binding.temperatureMax.text = getString(R.string.deg_celsius, decimalFormat.format(temperatureMax))
                binding.temperatureMin.text = getString(R.string.deg_celsius, decimalFormat.format(temperatureMin))
                binding.conditionText.text = conditionText
                binding.conditionIcon.setImageResource(WeatherIcon.getDrawableId(conditionIcon))
            }
        }

        private fun getString(resId: Int, vararg formatArgs: String): String {
            return itemView.context.getString(resId, *formatArgs)
        }

        companion object {
            @SuppressLint("SimpleDateFormat")
            private val dateFormat = SimpleDateFormat("EEEE d")
            private val decimalFormat = DecimalFormat("0.#")
        }
    }
}