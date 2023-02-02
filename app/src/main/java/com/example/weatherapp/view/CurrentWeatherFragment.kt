package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentCurrentWeatherBinding
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private lateinit var viewModel: CurrentWeatherViewModel
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CurrentWeatherViewModel::class.java]
        lifecycleScope.launch { viewModel.refresh() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        val refreshView = binding.swipeRefresh.apply {
            setOnRefreshListener {
                lifecycleScope.launch { viewModel.refresh() }
            }
        }
        val decimal = DecimalFormat("0.#")

        viewModel.refreshing.observe(viewLifecycleOwner) {
            refreshView.isRefreshing = it
        }

        viewModel.current.observe(viewLifecycleOwner) {
            with(it) {
                val iconId = WeatherIcon.getDrawable(conditionIcon, isDay)
                binding.temperature.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0)
                binding.temperature.text = getString(R.string.deg_celsius, decimal.format(temperature))
                binding.conditionText.text = conditionText
                binding.uv.text = decimal.format(uv)
                binding.windSpeed.text = getString(R.string.km_hour, decimal.format(windSpeed))
                binding.humidity.text = getString(R.string.percent, decimal.format(humidity))
                binding.currentWeatherLayout.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.currentWeatherLayout.visibility = View.GONE
            Snackbar.make(view, it?:"Error", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

        return view
    }
}