package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.data.local.WeatherDatabase

class WeatherApplication : Application() {
    val database: WeatherDatabase by lazy {
        WeatherDatabase.getDatabase(this)
    }
}
