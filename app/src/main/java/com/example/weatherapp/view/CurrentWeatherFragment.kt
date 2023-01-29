package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentCurrentWeatherBinding
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
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