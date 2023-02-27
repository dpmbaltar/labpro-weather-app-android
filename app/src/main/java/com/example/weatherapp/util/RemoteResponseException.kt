package com.example.weatherapp.util

import java.io.IOException

class RemoteResponseException(
    message: String?,
    val errorBody: Map<String, Any>? = null
) : IOException(message)
