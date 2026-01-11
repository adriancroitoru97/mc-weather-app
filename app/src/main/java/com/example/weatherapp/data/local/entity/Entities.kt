package com.example.weatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapp.data.local.Converters

@Entity(tableName = "cities")
@TypeConverters(Converters::class)
data class CityEntity(
    @PrimaryKey
    val cityId: Int,
    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDegree: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val timestamp: Long,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "forecasts")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityId: Int,
    val timestamp: Long,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDegree: Int,
    val cloudiness: Int,
    val dateText: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
