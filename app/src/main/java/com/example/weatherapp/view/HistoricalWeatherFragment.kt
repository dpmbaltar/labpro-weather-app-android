package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.HistoricalWeatherPagingDataAdapter
import com.example.weatherapp.databinding.FragmentHistoricalWeatherBinding
import com.example.weatherapp.viewmodel.HistoricalWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoricalWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HistoricalWeatherFragment()
    }

    private lateinit var viewModel: HistoricalWeatherViewModel
    private var _binding: FragmentHistoricalWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HistoricalWeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricalWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        val pagingAdapter = HistoricalWeatherPagingDataAdapter()
        val recyclerView = binding.historicalWeather
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pagingAdapter

        lifecycleScope.launch {
            viewModel.weatherFlow.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }

        return view
    }
}