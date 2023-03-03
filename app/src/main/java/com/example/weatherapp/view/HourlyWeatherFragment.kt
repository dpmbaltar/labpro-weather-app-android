package com.example.weatherapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.HourlyWeatherAdapter
import com.example.weatherapp.databinding.FragmentHourlyWeatherBinding
import com.example.weatherapp.util.hourMinutes
import com.example.weatherapp.util.isoDate
import com.example.weatherapp.util.isoDateTimeSimple
import com.example.weatherapp.util.weekdayDate
import com.example.weatherapp.viewmodel.HourlyWeatherViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HourlyWeatherFragment : BottomSheetDialogFragment() {

    companion object {

        const val LATITUDE_NAME = "latitude"
        const val LONGITUDE_NAME = "longitude"
        const val DATE_NAME = "date"
        const val SUNRISE_NAME = "sunrise"
        const val SUNSET_NAME = "sunset"

        fun newInstance(
            latitude: Double,
            longitude: Double,
            date: String,
            sunrise: String,
            sunset: String
        ) = HourlyWeatherFragment().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE_NAME, latitude)
                putDouble(LONGITUDE_NAME, longitude)
                putString(DATE_NAME, date)
                putString(SUNRISE_NAME, sunrise)
                putString(SUNSET_NAME, sunset)
            }
        }
    }

    private lateinit var viewModel: HourlyWeatherViewModel
    private var _binding: FragmentHourlyWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]
        arguments?.let {
            val latitude = it.getDouble(LATITUDE_NAME)
            val longitude = it.getDouble(LONGITUDE_NAME)
            val date = it.getString(DATE_NAME)!!.isoDate()

            lifecycleScope.launch {
                viewModel.loadHourlyWeather(
                    latitude,
                    longitude,
                    Calendar.getInstance().apply { time = date }
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHourlyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        val hourlyWeatherAdapter = HourlyWeatherAdapter()

        binding.hourlyWeather.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hourlyWeatherAdapter
        }

        arguments?.let { args ->
            val date = args.getString(DATE_NAME)
            val sunrise = args.getString(SUNRISE_NAME)
            val sunset = args.getString(SUNSET_NAME)

            try {
                binding.date.text = date!!.isoDate().weekdayDate()
                binding.sunrise.text = sunrise!!.isoDateTimeSimple().hourMinutes()
                binding.sunset.text = sunset!!.isoDateTimeSimple().hourMinutes()
            } catch (e: Exception) {
                binding.date.text = date
                binding.sunrise.text = sunrise
                binding.sunset.text = sunset
                Log.d(HourlyWeatherFragment::class.java.simpleName, e.message, e)
            }
        }

        lifecycleScope.launch {
            viewModel.hourlyWeather.collect {
                hourlyWeatherAdapter.submitList(it)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}