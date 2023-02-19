package com.example.weatherapp.adapter

import android.util.Log
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
                return oldItem.time == newItem.time
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

        private val decimal = DecimalFormat("0.#")
        private val dateIn = SimpleDateFormat("yyyy-MM-dd")
        private val dateOut = SimpleDateFormat("EEEE d")

        fun bind(dailyWeather: DailyWeather) {
            with(binding) {
                date.text = parseTime(dailyWeather.time)
                temperatureMax.text = getString(R.string.deg_celsius, decimal.format(dailyWeather.temperatureMax))
                temperatureMin.text = getString(R.string.deg_celsius, decimal.format(dailyWeather.temperatureMin))
                conditionText.text = dailyWeather.conditionText
                conditionIcon.setImageResource(WeatherIcon.getDrawableId(dailyWeather.conditionIcon))
            }
        }

        private fun getString(resId: Int, vararg formatArgs: String): String {
            return itemView.context.getString(resId, *formatArgs)
        }

        private fun parseTime(dateString: String): String {
            return try {
                val date = dateIn.parse(dateString)
                dateOut.format(date)
            } catch (e: Exception) {
                Log.d("DailyWeatherAdapter.parseTime()", e.localizedMessage)
                dateString
            }
        }
    }
}