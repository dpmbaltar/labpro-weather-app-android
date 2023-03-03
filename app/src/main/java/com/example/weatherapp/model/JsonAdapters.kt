package com.example.weatherapp.model

import com.example.weatherapp.util.isoDate
import com.example.weatherapp.util.isoDateTimeSimple
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.*

class JsonAdapters {

    class DateTimeAdapter : TypeAdapter<Date>() {

        override fun read(reader: JsonReader): Date =
            reader.nextString().isoDateTimeSimple()

        override fun write(writer: JsonWriter, value: Date): Unit =
            writer.value(value.isoDateTimeSimple()).let { }
    }

    class DateAdapter : TypeAdapter<Date>() {

        override fun read(reader: JsonReader): Date =
            reader.nextString().isoDate()

        override fun write(writer: JsonWriter, value: Date): Unit =
            writer.value(value.isoDate()).let { }
    }
}