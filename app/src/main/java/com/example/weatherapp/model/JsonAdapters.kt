package com.example.weatherapp.model

import android.annotation.SuppressLint
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class JsonAdapters {

    class DateTimeWithoutSecondsAdapter : TypeAdapter<Date>() {

        @SuppressLint("SimpleDateFormat")
        private val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")

        override fun read(`in`: JsonReader?): Date = dateFormat.parse(`in`!!.nextString())!!

        override fun write(out: JsonWriter?, value: Date?) {
            out!!.value(value?.let { dateFormat.format(it) })
        }
    }
}