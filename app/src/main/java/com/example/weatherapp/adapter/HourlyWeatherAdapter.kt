package com.example.weatherapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HourlyWeatherItemBinding
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.util.WeatherIcon
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class HourlyWeatherAdapter() : ListAdapter<HourlyWeather, HourlyWeatherAdapter.HourlyWeatherViewHolder>(
    DiffCallback
) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<HourlyWeather>() {

            override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val viewHolder = HourlyWeatherViewHolder(
            HourlyWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HourlyWeatherViewHolder(
        private var binding: HourlyWeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val decimal = DecimalFormat("0.#")
        private val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        private val time = SimpleDateFormat("HH:mm")

        fun bind(hourlyWeather: HourlyWeather) {
            with(binding) {
                time.text = parseTime(hourlyWeather.time)
                conditionIcon.setImageResource(WeatherIcon.getDrawableId(hourlyWeather.conditionIcon))
                temperature.text = getString(R.string.deg_celsius, decimal.format(hourlyWeather.temperature))
                precipitation.text = getString(R.string.percent, decimal.format(hourlyWeather.precipitation))
                windSpeed.text = getString(R.string.km_hour, decimal.format(hourlyWeather.windSpeed))
            }
        }

        private fun getString(resId: Int, vararg formatArgs: String): String {
            return itemView.context.getString(resId, *formatArgs)
        }

        private fun parseTime(dateString: String): String {
            return try {
                val date = date.parse(dateString)
                time.format(date)
            } catch (e: Exception) {
                Log.d("HourlyWeatherAdapter.parseTime()", e.localizedMessage)
                dateString
            }
        }
    }
}