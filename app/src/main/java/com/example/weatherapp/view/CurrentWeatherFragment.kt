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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentCurrentWeatherBinding
import com.example.weatherapp.util.ConnectionException
import com.example.weatherapp.util.RemoteResponseException
import com.example.weatherapp.util.WeatherIcon
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel.CurrentWeatherUiState
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrentWeatherFragment : Fragment() {

    private lateinit var refreshView: SwipeRefreshLayout
    private lateinit var viewModel: CurrentWeatherViewModel
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CurrentWeatherViewModel::class.java]
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
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)

        refreshView = binding.swipeRefresh.apply {
            setOnRefreshListener {
                lifecycleScope.launch { viewModel.refresh() }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> showLoading()
                    is UiState.Error -> showError(uiState.throwable)
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentWeather.collect {
                showCurrentWeather(it)
            }
        }

        return binding.root
    }

    private fun showCurrentWeather(data: CurrentWeatherUiState) {
        with(data) {
            val icon = WeatherIcon.getDrawableId(conditionIcon, isDay)
            binding.temperature.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
            binding.temperature.text = temperature
            binding.conditionText.text = conditionText
            binding.uv.text = uv
            binding.windSpeed.text = windSpeed
            binding.humidity.text = humidity
            binding.time.text = time
            binding.locationName.text = locationName
            binding.currentWeatherLayout.visibility = View.VISIBLE
        }.also {
            showLoading(false)
        }
    }

    private fun showLoading(isRefreshing: Boolean = true) {
        refreshView.isRefreshing = isRefreshing
    }

    private fun showError(throwable: Throwable?) {
        Log.d(TAG, throwable?.localizedMessage, throwable)

        val message = when (throwable) {
            is ConnectionException -> getString(R.string.connection_exception)
            is RemoteResponseException -> getString(R.string.remote_response_exception)
            else -> getString(R.string.unknown_exception)
        }

        activity?.currentFocus?.let { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
                .also { showLoading(false) }
        }
    }

    companion object {

        private val TAG = CurrentWeatherFragment::class.java.simpleName

        fun newInstance() = CurrentWeatherFragment()
    }
}