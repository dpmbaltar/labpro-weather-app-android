package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentDailyWeatherBinding
import com.example.weatherapp.viewmodel.DailyWeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = DailyWeatherFragment()
    }

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

        val dailyWeatherAdapter = DailyWeatherAdapter {
            Snackbar.make(binding.root, "Hola", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val recyclerView = binding.dailyWeather
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = dailyWeatherAdapter

        viewModel.daily.observe(viewLifecycleOwner) {
            dailyWeatherAdapter.submitList(it)
        }

        return binding.root
    }

}