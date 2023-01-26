package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentWeatherCurrentBinding
import com.example.weatherapp.viewmodel.WeatherCurrentViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class WeatherCurrentFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherCurrentFragment()
    }

    private lateinit var viewModel: WeatherCurrentViewModel
    private var _binding: FragmentWeatherCurrentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherCurrentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherCurrentBinding.inflate(inflater, container, false)
        val root = binding.root
        val decimal = DecimalFormat("0.#")

        viewModel.current.observe(viewLifecycleOwner) {
            with (binding) {
                temperature.text = getString(R.string.deg_celsius, decimal.format(it.temperature))
                uv.text = decimal.format(it.uv)
                windspeed.text = getString(R.string.km_hour, decimal.format(it.windSpeed))
                humidity.text = getString(R.string.percent, decimal.format(it.humidity))
                loading.visibility = View.GONE
            }
        }

        viewModel.conditionText.observe(viewLifecycleOwner) {
            binding.conditionText.text = it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Snackbar.make(root, it?:"Error", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

        return root
    }

}