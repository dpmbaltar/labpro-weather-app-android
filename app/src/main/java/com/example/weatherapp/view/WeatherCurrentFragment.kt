package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentWeatherCurrentBinding
import com.example.weatherapp.viewmodel.WeatherCurrentViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherCurrentFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherCurrentFragment()
    }

    private lateinit var viewModel: WeatherCurrentViewModel
    private var _binding: FragmentWeatherCurrentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherCurrentViewModel::class.java].apply {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherCurrentBinding.inflate(inflater, container, false)
        val root = binding.root

        viewModel.current.observe(viewLifecycleOwner) {
            binding.temperature.text = "%.1f °C".format(it.temperature)
            binding.uv.text = "%d".format(it.uv)
            binding.windspeed.text = "%.1f km/h".format(it.windSpeed)
            binding.humidity.text = "%.1f %%".format(it.humidity)
            binding.loading.visibility = View.GONE
        }

        viewModel.conditionText.observe(viewLifecycleOwner) {
            binding.conditionText.text = it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Snackbar.make(root, it?:"Error", Snackbar.LENGTH_LONG)
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