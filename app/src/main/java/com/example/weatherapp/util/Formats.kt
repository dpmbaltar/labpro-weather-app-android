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
private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd")

@SuppressLint("SimpleDateFormat")
private val isoDateTimeSimpleFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")

@SuppressLint("SimpleDateFormat")
private val weekdayDateMonthFormat = SimpleDateFormat("EEEE, d MMM")

@SuppressLint("SimpleDateFormat")
private val weekdayDateFormat = SimpleDateFormat("EEEE d")

@SuppressLint("SimpleDateFormat")
private val hourMinutesFormat = SimpleDateFormat("HH:mm")

fun String.percent(): String = this.plus(SPACE).plus(PERCENT)
fun String.degreesCelsius(): String = this.plus(SPACE).plus(DEGREE_CELSIUS)
fun String.kilometersPerHour(): String = this.plus(SPACE).plus(KILOMETER_PER_HOUR)
fun String.isoDate(): Date = isoDateFormat.parse(this)!!
fun String.isoDateTimeSimple(): Date = isoDateTimeSimpleFormat.parse(this)!!

fun Double.decimal(): String = decimalFormat.format(this)
fun Double.percent(): String = this.decimal().percent()
fun Double.degreesCelsius(): String = this.decimal().degreesCelsius()
fun Double.kilometersPerHour(): String = this.decimal().kilometersPerHour()

fun Int.decimal(): String = decimalFormat.format(this)
fun Int.percent(): String = this.decimal().percent()
fun Int.degreesCelsius(): String = this.decimal().degreesCelsius()
fun Int.kilometersPerHour(): String = this.decimal().kilometersPerHour()
fun Int.weatherIcon(): Int = WeatherIcon.getDrawableId(this)

fun Calendar.isoDate(): String = this.time.isoDate()
fun Calendar.weekdayDateMonth(): String = this.time.weekdayDateMonth()
fun Calendar.weekdayDate(): String = this.time.weekdayDate()
fun Calendar.hourMinutes(): String = this.time.hourMinutes()

fun Date.isoDate(): String = isoDateFormat.format(this)
fun Date.isoDateTimeSimple(): String = isoDateTimeSimpleFormat.format(this)
fun Date.weekdayDateMonth(): String = weekdayDateMonthFormat.format(this)
fun Date.weekdayDate(): String = weekdayDateFormat.format(this)
fun Date.hourMinutes(): String = hourMinutesFormat.format(this)