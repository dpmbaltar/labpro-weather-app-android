package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.DailyWeatherAdapter
import com.example.weatherapp.databinding.FragmentDailyWeatherBinding
import com.example.weatherapp.viewmodel.DailyWeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DailyWeatherFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null
    private lateinit var viewModel: DailyWeatherViewModel
    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location = it?.apply {
                lifecycleScope.launch {
                    viewModel.loadDailyWeather(latitude, longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dailyWeatherAdapter = DailyWeatherAdapter { _, position ->
            viewModel.location.value?.let { location ->
                viewModel.daily.value?.let {
                    val daily = it[position]
                    val action = DailyWeatherFragmentDirections
                        .actionDailyWeatherFragmentToHourlyWeatherFragment(
                            latitude = location.latitude.toFloat(),
                            longitude = location.longitude.toFloat(),
                            date = dateFormat.format(daily.time),
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