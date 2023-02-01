package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.weatherapp.databinding.FragmentDailyWeatherNavHostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyWeatherNavHostFragment : Fragment() {

    companion object {
        fun newInstance() = DailyWeatherNavHostFragment()
    }

    private lateinit var navController: NavController
    private var _binding: FragmentDailyWeatherNavHostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailyWeatherNavHostBinding.inflate(inflater, container, false)
        val view = binding.root
        navController = view.findNavController()

        return view
    }
}