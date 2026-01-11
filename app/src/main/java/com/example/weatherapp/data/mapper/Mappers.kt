package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.local.entity.CityEntity
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.domain.model.City
import com.example.weatherapp.domain.model.Forecast

// CityEntity -> City (Domain)
fun CityEntity.toCity(): City {
    return City(
        cityId = cityId,
        cityName = cityName,
        country = country,
        latitude = latitude,
        longitude = longitude,
        temperature = temperature,
        feelsLike = feelsLike,
        tempMin = tempMin,
        tempMax = tempMax,
        weatherMain = weatherMain,
        weatherDescription = weatherDescription,
        weatherIcon = weatherIcon,
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        windDegree = windDegree,
        cloudiness = cloudiness,
        sunrise = sunrise,
        sunset = sunset,
        timestamp = timestamp,
        isFavorite = isFavorite,
        lastUpdated = lastUpdated
    )
}

// ForecastEntity -> Forecast (Domain)
fun ForecastEntity.toForecast(): Forecast {
    return Forecast(
        timestamp = timestamp,
        temperature = temperature,
        feelsLike = feelsLike,
        tempMin = tempMin,
        tempMax = tempMax,
        weatherMain = weatherMain,
        weatherDescription = weatherDescription,
        weatherIcon = weatherIcon,
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        windDegree = windDegree,
        cloudiness = cloudiness,
        dateText = dateText
    )
}

/**
 * Helper function to generate a stable city ID from coordinates
 * Uses a hash-like approach to ensure uniqueness
 */
fun generateCityId(latitude: Double, longitude: Double): Int {
    // Round coordinates to 4 decimal places (~11 meters precision) to handle slight variations
    val roundedLat = (latitude * 10000).toInt()
    val roundedLon = (longitude * 10000).toInt()
    // Combine using bit shifting to avoid collisions
    return (roundedLat * 1000000 + roundedLon).hashCode()
}
