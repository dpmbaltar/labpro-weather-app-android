package com.example.weatherapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.DailyWeatherItemBinding
import com.example.weatherapp.databinding.LoadingItemBinding
import com.example.weatherapp.util.WeatherIcon
import com.example.weatherapp.viewmodel.HistoricalWeatherItem


class HistoricalWeatherAdapter(
    private val itemList: List<HistoricalWeatherItem?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LOADING) {
            LoadingViewHolder(
                LoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            DailyWeatherViewHolder(
                DailyWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
        /*viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position), position)
        }

        return viewHolder*/
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DailyWeatherViewHolder) {
            (holder as DailyWeatherViewHolder?)?.let { populateItemRows(it, position) }
        } else if (holder is LoadingViewHolder) {
            (holder as LoadingViewHolder?)?.let { showLoadingView(it, position) }
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class DailyWeatherViewHolder(
        private var binding: DailyWeatherItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(weather: HistoricalWeatherItem) {
            with(binding) {
                date.text = weather.time
                temperatureMax.text = weather.temperatureMax
                temperatureMin.text = weather.temperatureMin
                conditionText.text = weather.conditionText
                conditionIcon.setImageResource(WeatherIcon.getDrawableId(weather.conditionIcon))
            }
        }
    }

    private class LoadingViewHolder(
        private var binding: LoadingItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var progressBar: ProgressBar = binding.progressBar
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: DailyWeatherViewHolder, position: Int) {
        itemList[position]?.let { viewHolder.bind(it) }
    }
}