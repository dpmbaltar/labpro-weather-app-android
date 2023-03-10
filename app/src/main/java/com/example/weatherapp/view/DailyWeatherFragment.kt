package com.example.weatherapp.view

import android.os.Bundle
import android.util.Log
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
import com.example.weatherapp.util.ConnectionException
import com.example.weatherapp.util.RemoteResponseException
import com.example.weatherapp.viewmodel.DailyWeatherViewModel
import com.example.weatherapp.viewmodel.DailyWeatherViewModel.DailyWeatherUiModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DailyWeatherFragment : Fragment() {

    companion object {
        private val TAG = DailyWeatherFragment::class.java.simpleName
        fun newInstance() = DailyWeatherFragment()
    }

    private var hourlyWeatherFragment: HourlyWeatherFragment? = null
    private lateinit var dailyWeatherAdapter: DailyWeatherAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshView: SwipeRefreshLayout
    private lateinit var viewModel: DailyWeatherViewModel
    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DailyWeatherViewModel::class.java]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.refresh()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyWeatherBinding.inflate(inflater, container, false)
        val view = binding.root

        dailyWeatherAdapter = DailyWeatherAdapter { dailyWeather, _ ->
            hourlyWeatherFragment = with(dailyWeather) {
                HourlyWeatherFragment.newInstance(latitude, longitude, date, sunrise, sunset)
            }.apply {
                this@DailyWeatherFragment.activity?.supportFragmentManager?.let {
                    show(it, tag)
                }
            }
        }

        recyclerView = binding.dailyWeather.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyWeatherAdapter
        }

        refreshView = binding.swipeRefresh.apply {
            setOnRefreshListener {
                lifecycleScope.launch { viewModel.refresh() }
            }
        }

        lifecycleScope.launch { viewModel.dailyWeather.collect { showDailyWeather(it) } }
        lifecycleScope.launch { viewModel.loading.collect { showLoading(it) } }
        lifecycleScope.launch { viewModel.error.collect { showError(it) } }

        return view
    }

    private fun showDailyWeather(data: List<DailyWeatherUiModel>) {
        dailyWeatherAdapter.submitList(data)
    }

    private fun showLoading(isRefreshing: Boolean = true) {
        refreshView.isRefreshing = isRefreshing
    }

    private fun showError(throwable: Throwable?) {
        Log.d(TAG, throwable?.message, throwable)

        val message = when (throwable) {
            is ConnectionException -> getString(R.string.connection_exception)
            is RemoteResponseException -> getString(R.string.remote_response_exception)
            else -> getString(R.string.unknown_exception)
        }

        hourlyWeatherFragment?.dismiss()
        activity?.findViewById<View>(R.id.main_layout)?.let { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }
}