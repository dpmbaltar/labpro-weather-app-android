package com.example.weatherapp.util

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

const val SPACE = " "
const val PERCENT = "%"
const val DEGREE_CELSIUS = "°C"
const val KILOMETER_PER_HOUR = "km/h"

private val decimalFormat = DecimalFormat("0.#")

@SuppressLint("SimpleDateFormat")
private val weekdayDateMonthFormat = SimpleDateFormat("EEEE, d MMM")

@SuppressLint("SimpleDateFormat")
private val weekdayDateFormat = SimpleDateFormat("EEEE d")

@SuppressLint("SimpleDateFormat")
private val hourMinutesFormat = SimpleDateFormat("HH:mm")

fun String.percent(): String = this.plus(SPACE).plus(PERCENT)
fun String.degreesCelsius(): String = this.plus(SPACE).plus(DEGREE_CELSIUS)
fun String.kilometersPerHour(): String = this.plus(SPACE).plus(KILOMETER_PER_HOUR)

fun Double.decimal(): String = decimalFormat.format(this)
fun Double.percent(): String = this.decimal().percent()
fun Double.degreesCelsius(): String = this.decimal().degreesCelsius()
fun Double.kilometersPerHour(): String = this.decimal().kilometersPerHour()

fun Int.decimal(): String = decimalFormat.format(this)
fun Int.percent(): String = this.decimal().percent()
fun Int.degreesCelsius(): String = this.decimal().degreesCelsius()
fun Int.kilometersPerHour(): String = this.decimal().kilometersPerHour()

fun Date.weekdayDateMonth(): String = weekdayDateMonthFormat.format(this)
fun Date.weekdayDate(): String = weekdayDateFormat.format(this)
fun Date.hourMinutes(): String = hourMinutesFormat.format(this)