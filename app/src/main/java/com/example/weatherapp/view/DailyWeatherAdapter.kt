package com.example.weatherapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.model.DailyWeather
import java.text.DecimalFormat

class DailyWeatherAdapter(
    private val onItemClicked: (DailyWeather) -> Unit
) : ListAdapter<DailyWeather, DailyWeatherAdapter.DailyWeatherViewHolder>(DiffCallback) {

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
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

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

    class DailyWeatherViewHolder(
        private var binding: DailyWeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val decimal = DecimalFormat("0.#")

        fun bind(dailyWeather: DailyWeather) {
            with(binding) {
                // TODO: Bind other fields
                temperatureMax.text =
                    getString(R.string.deg_celsius, decimal.format(dailyWeather.temperatureMax))
            }
        }

        private fun getString(resId: Int, vararg formatArgs: String): String {
            return itemView.context.getString(resId, *formatArgs)
        }

    }
}