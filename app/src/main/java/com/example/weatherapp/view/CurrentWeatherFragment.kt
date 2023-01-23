package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentCurrentWeatherBinding
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel
import com.google.android.material.snackbar.Snackbar

class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private lateinit var viewModel: CurrentWeatherViewModel
    private var _binding: FragmentCurrentWeatherBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CurrentWeatherViewModel::class.java].apply {
            getConditions()
            getCurrentWeather()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        val root = binding.root

        viewModel.weather.observe(viewLifecycleOwner) {
            binding.temperature.text = "%.1f °C".format(it.temperature)
            binding.uv.text = "%d".format(it.uv)
            binding.windspeed.text = "%.1f km/h".format(it.windSpeed)
            binding.humidity.text = "%.1f %%".format(it.humidity)
            binding.conditionText.text = viewModel.conditionText(it.weatherCode, it.isDay)
            binding.loading.visibility = View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Snackbar.make(root, "Error", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        return root
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

}