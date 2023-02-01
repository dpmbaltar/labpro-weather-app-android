package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.weatherapp.databinding.FragmentHourlyWeatherBinding
import com.example.weatherapp.viewmodel.HourlyWeatherViewModel

class HourlyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HourlyWeatherFragment()
    }

    private lateinit var viewModel: HourlyWeatherViewModel
    private var _binding: FragmentHourlyWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHourlyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.backButton.setOnClickListener { view.findNavController().popBackStack() }

        return view
    }
}