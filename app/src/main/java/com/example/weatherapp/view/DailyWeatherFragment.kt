package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentDailyWeatherBinding
import com.example.weatherapp.viewmodel.DailyWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DailyWeatherFragment : Fragment() {

    private lateinit var viewModel: DailyWeatherViewModel
    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root

        val dailyWeatherAdapter = DailyWeatherAdapter { _, position ->
            viewModel.location.value?.let { location ->
                viewModel.daily.value?.let {
                    val daily = it[position]
                    val date = daily.time ?: Calendar.getInstance().toString()
                    val action = DailyWeatherFragmentDirections
                        .actionDailyWeatherFragmentToHourlyWeatherFragment(
                            latitude = location.latitude.toFloat(),
                            longitude = location.longitude.toFloat(),
                            date = date,
                            sunrise = daily.sunrise,
                            sunset = daily.sunset
                        )
                    view.findNavController().navigate(action)
                }
            }
        }

        val recyclerView = binding.dailyWeather
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = dailyWeatherAdapter

        viewModel.daily.observe(viewLifecycleOwner) {
            dailyWeatherAdapter.submitList(it)
        }

        return view
    }
}