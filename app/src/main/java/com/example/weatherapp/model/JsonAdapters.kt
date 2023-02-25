package com.example.weatherapp.model

import android.annotation.SuppressLint
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class JsonAdapters {

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

        @SuppressLint("SimpleDateFormat")
        private val dateTimeFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
    }

    class DateTimeAdapter : TypeAdapter<Date>() {

        override fun read(reader: JsonReader): Date =
            dateTimeFormat.parse(reader.nextString())!!

        override fun write(writer: JsonWriter, value: Date): Unit =
            writer.value(dateTimeFormat.format(value)).let {  }
    }

    class DateAdapter : TypeAdapter<Date>() {

        override fun read(reader: JsonReader): Date =
            dateFormat.parse(reader.nextString())!!

        override fun write(writer: JsonWriter, value: Date): Unit =
            writer.value(dateFormat.format(value)).let {  }
    }
}