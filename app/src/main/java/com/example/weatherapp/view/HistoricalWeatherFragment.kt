package com.example.weatherapp.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.databinding.FragmentHistoricalWeatherBinding
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.viewmodel.HistoricalWeatherItem
import com.example.weatherapp.viewmodel.HistoricalWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.ThreadLocalRandom

@AndroidEntryPoint
class HistoricalWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HistoricalWeatherFragment()
    }

    private lateinit var viewModel: HistoricalWeatherViewModel
    private var _binding: FragmentHistoricalWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: Adapter<ViewHolder>
    private var rowsArrayList: ArrayList<HistoricalWeatherItem?> = ArrayList()
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HistoricalWeatherViewModel::class.java]
        //lifecycleScope.launch { viewModel.moreWeather() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoricalWeatherBinding.inflate(inflater, container, false)
        val view = binding.root

        //recyclerViewAdapter = HistoricalWeatherAdapter(rowsArrayList)
        val pagingAdapter = HistoricalWeatherPagingDataAdapter()
        recyclerView = binding.historicalWeather
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pagingAdapter
        //initScrollListener()
        //populateData()

        lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }


        /*viewModel.uiState.observe(viewLifecycleOwner) {
            rowsArrayList.addAll(it.historicalWeather)
        }*/

        return view
    }

    /*private fun populateData() {
        var i = 0
        while (i < 10) {
            rowsArrayList.add(createDailyWeather())
            i++
        }
    }*/

    private fun initScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        rowsArrayList.add(null)
        recyclerViewAdapter.notifyItemInserted(rowsArrayList.size - 1)
        recyclerView.scrollToPosition(rowsArrayList.size - 1)

        lifecycleScope.launch {
            viewModel.moreWeather()

            rowsArrayList.removeAt(rowsArrayList.size - 1)
            val scrollPosition: Int = rowsArrayList.size
            recyclerViewAdapter.notifyItemRemoved(scrollPosition)
            /*var currentSize = scrollPosition
            val nextLimit = currentSize + 7
            while (currentSize - 1 < nextLimit) {
                rowsArrayList.add(createDailyWeather())
                currentSize++
            }*/
            recyclerViewAdapter.notifyDataSetChanged()
            isLoading = false
        }

        /*val handler = Handler()
        handler.postDelayed(Runnable {
            rowsArrayList.removeAt(rowsArrayList.size - 1)
            val scrollPosition: Int = rowsArrayList.size
            recyclerViewAdapter.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            while (currentSize - 1 < nextLimit) {
                rowsArrayList.add(createDailyWeather())
                currentSize++
            }
            recyclerViewAdapter.notifyDataSetChanged()
            isLoading = false
        }, 2000)*/
    }

    /*private fun createDailyWeather() = DailyWeather(
        time = "2023-02-04",
        temperatureMax = ThreadLocalRandom.current().nextDouble(20.0, 40.0),
        temperatureMin = ThreadLocalRandom.current().nextDouble(10.0, 40.0),
        apparentTemperatureMax = ThreadLocalRandom.current().nextDouble(20.0, 40.0),
        apparentTemperatureMin = ThreadLocalRandom.current().nextDouble(10.0, 40.0),
        sunrise = "06:45",
        sunset = "20:47",
        precipitationSum = ThreadLocalRandom.current().nextDouble(0.0, 1.0),
        precipitationHours = ThreadLocalRandom.current().nextDouble(0.0, 24.0),
        windSpeedMax = ThreadLocalRandom.current().nextDouble(0.0, 100.0),
        windGustsMax = ThreadLocalRandom.current().nextDouble(0.0, 100.0),
        windDirection = ThreadLocalRandom.current().nextDouble(0.0, 360.0),
        conditionText = "Soleado",
        conditionIcon = 113
    )*/
}