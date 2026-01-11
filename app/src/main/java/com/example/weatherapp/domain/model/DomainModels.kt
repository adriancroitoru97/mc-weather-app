package com.example.weatherapp.domain.model

/**
 * Domain model representing a city with weather data
 */
data class City(
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
    val lastUpdated: Long
)

/**
 * Domain model representing weather forecast data
 */
data class Forecast(
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
    val dateText: String
)

/**
 * Result wrapper for operations
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
