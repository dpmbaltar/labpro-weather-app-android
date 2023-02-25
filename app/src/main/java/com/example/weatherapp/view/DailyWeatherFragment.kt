package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapp.R
import com.example.weatherapp.adapter.DailyWeatherAdapter
import com.example.weatherapp.databinding.FragmentDailyWeatherBinding
import com.example.weatherapp.model.DailyWeatherResponse
import com.example.weatherapp.viewmodel.DailyWeatherViewModel
import com.example.weatherapp.viewmodel.DailyWeatherViewModel.UiState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DailyWeatherFragment : Fragment() {

    private lateinit var dailyWeatherAdapter: DailyWeatherAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshView: SwipeRefreshLayout
    private lateinit var viewModel: DailyWeatherViewModel
    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.refresh()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        //val navController = view.findNavController()
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        dailyWeatherAdapter = DailyWeatherAdapter { _, _ ->
            val bottomSheet = BottomSheetDialog(requireContext())
            bottomSheet.setContentView(R.layout.loading_item)
            bottomSheet.show()
            /*lifecycleScope.launch {
                viewModel.uiState.filterIsInstance<UiState.Success>().collect {
                    navController.navigate(
                        DailyWeatherFragmentDirections
                            .actionDailyWeatherFragmentToHourlyWeatherFragment(
                                latitude = it.data.location.latitude.toFloat(),
                                longitude = it.data.location.longitude.toFloat(),
                                date = dateFormat.format(daily.time),
                                sunrise = daily.sunrise,
                                sunset = daily.sunset
                            )
                    )
                }
            }*/
        }

        recyclerView = binding.dailyWeather
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = dailyWeatherAdapter
        refreshView = binding.swipeRefresh.apply {
            setOnRefreshListener {
                lifecycleScope.launch { viewModel.refresh() }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showDailyWeather(uiState.data)
                    is UiState.Error -> showError(uiState.throwable)
                }
            }
        }

        return view
    }

    private fun showLoading(isRefreshing: Boolean = true) {
        refreshView.isRefreshing = isRefreshing
    }

    private fun showDailyWeather(data: DailyWeatherResponse) {
        dailyWeatherAdapter.submitList(data.daily).also {
            showLoading(false)
        }
    }

    private fun showError(throwable: Throwable?) {
        Snackbar.make(binding.root, throwable?.localizedMessage ?: "Error", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
            .also { showLoading(false) }
    }
}