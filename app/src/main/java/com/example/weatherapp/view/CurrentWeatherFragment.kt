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
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import com.example.weatherapp.databinding.FragmentCurrentWeatherBinding
import com.example.weatherapp.util.WeatherIcon
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null
    private lateinit var viewModel: CurrentWeatherViewModel
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CurrentWeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location = it?.apply {
                lifecycleScope.launch {
                    viewModel.refresh(latitude, longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        val refreshView = binding.swipeRefresh.apply {
            setOnRefreshListener {
                location?.apply {
                    lifecycleScope.launch { viewModel.refresh(latitude, longitude) }
                }
            }
        }

        viewModel.uiState.map {
            it.isRefreshing
        }.distinctUntilChanged().observe(viewLifecycleOwner) {
            refreshView.isRefreshing = it
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            if (it.isFailed) {
                binding.currentWeatherLayout.visibility = View.GONE
                Snackbar.make(view, it.lastError, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            }
        }

        viewModel.currentWeather.observe(viewLifecycleOwner) {
            with(it) {
                val iconId = WeatherIcon.getDrawableId(conditionIcon, isDay)
                binding.temperature.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0)
                binding.temperature.text = temperature
                binding.conditionText.text = conditionText
                binding.uv.text = uv
                binding.windSpeed.text = windSpeed
                binding.humidity.text = humidity
                binding.time.text = currentTime
                binding.locationName.text = locationName
                binding.currentWeatherLayout.visibility = View.VISIBLE
            }
        }

        return view
    }
}