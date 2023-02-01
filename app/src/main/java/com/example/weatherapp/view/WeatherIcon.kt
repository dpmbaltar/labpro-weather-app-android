package com.example.weatherapp.view

import com.example.weatherapp.R

class WeatherIcon {

    companion object {
        fun getDrawable(conditionIcon: Int, isDay: Boolean = true): Int {
            return if (isDay)
                Day.getDrawable(conditionIcon)
            else
                Night.getDrawable(conditionIcon)
        }
    }

    class Day {
        companion object {
            fun getDrawable(conditionIcon: Int): Int {
                return when (conditionIcon) {
                    113 -> R.drawable.wi_day_113
                    116 -> R.drawable.wi_day_116
                    122 -> R.drawable.wi_day_122
                    143 -> R.drawable.wi_day_143
                    266 -> R.drawable.wi_day_266
                    353 -> R.drawable.wi_day_353
                    else -> R.drawable.wi_unknown
                }
            }
        }
    }

    class Night {
        companion object {
            fun getDrawable(conditionIcon: Int): Int {
                return when (conditionIcon) {
                    113 -> R.drawable.wi_night_113
                    116 -> R.drawable.wi_night_116
                    122 -> R.drawable.wi_night_122
                    143 -> R.drawable.wi_night_143
                    266 -> R.drawable.wi_night_266
                    353 -> R.drawable.wi_night_353
                    else -> R.drawable.wi_unknown
                }
            }
        }
    }
}