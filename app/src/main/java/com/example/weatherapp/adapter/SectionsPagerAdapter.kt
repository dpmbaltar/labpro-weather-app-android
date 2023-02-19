package com.example.weatherapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.weatherapp.R
import com.example.weatherapp.view.*

private val TAB_TITLES = arrayOf(
    R.string.current,
    R.string.forecast,
    R.string.history
)

class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CurrentWeatherFragment.newInstance()
            1 -> DailyWeatherNavHostFragment.newInstance()
            2 -> HistoricalWeatherFragment.newInstance()
            else -> PlaceholderFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount() = 3
}