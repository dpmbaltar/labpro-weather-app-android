package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.HourlyWeatherAdapter
import com.example.weatherapp.databinding.FragmentHourlyWeatherBinding
import com.example.weatherapp.viewmodel.HourlyWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HourlyWeatherFragment : Fragment() {

    companion object {
        var LATITUDE_NAME = "latitude"
        val LONGITUDE_NAME = "longitude"
        val DATE_NAME = "date"
        val SUNRISE_NAME = "sunrise"
        val SUNSET_NAME = "sunset"
    }

    private lateinit var viewModel: HourlyWeatherViewModel
    private var _binding: FragmentHourlyWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HourlyWeatherViewModel::class.java]
        arguments?.let {
            val latitude = it.getFloat(LATITUDE_NAME)
            val longitude = it.getFloat(LONGITUDE_NAME)
            val today = Calendar.getInstance()
            viewModel.fetchHourlyWeather(latitude.toDouble(), longitude.toDouble(), today)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHourlyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        val hourlyWeatherAdapter = HourlyWeatherAdapter()
        val recyclerView = binding.hourlyWeather
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = hourlyWeatherAdapter

        val dateFormat = SimpleDateFormat()
        val formats = arrayOf(
            "yyyy-MM-dd",
            "EEEE d",
            "yyyy-MM-dd'T'HH:mm",
            "HH:mm"
        )

        arguments?.let { args ->
            val time = args.getString(DATE_NAME)
            val sunrise = args.getString(SUNRISE_NAME)
            val sunset = args.getString(SUNSET_NAME)
            var date: Date?

            try {
                dateFormat.applyLocalizedPattern(formats[0])
                date = dateFormat.parse(time)
                dateFormat.applyLocalizedPattern(formats[1])
                binding.date.text = dateFormat.format(date)

                dateFormat.applyLocalizedPattern(formats[2])
                date = dateFormat.parse(sunrise)
                dateFormat.applyLocalizedPattern(formats[3])
                binding.sunrise.text = dateFormat.format(date)

                dateFormat.applyLocalizedPattern(formats[2])
                date = dateFormat.parse(sunset)
                dateFormat.applyLocalizedPattern(formats[3])
                binding.sunset.text = dateFormat.format(date)
            } catch (e: Exception) {
                binding.date.text = time
                binding.sunrise.text = sunrise
                binding.sunset.text = sunset
                Log.d("HourlyWeatherFragment.onCreateView()", e.localizedMessage)
            }
        }

        viewModel.hourly.observe(viewLifecycleOwner) {
            hourlyWeatherAdapter.submitList(it)
        }

        binding.backButton.setOnClickListener { view.findNavController().popBackStack() }

        return view
    }
}