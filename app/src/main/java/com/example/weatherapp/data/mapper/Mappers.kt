package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.local.entity.CityEntity
import com.example.weatherapp.data.local.entity.ForecastEntity
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.GeocodingResponse
import com.example.weatherapp.data.model.OneCallDaily
import com.example.weatherapp.data.model.OneCallHourly
import com.example.weatherapp.data.model.OneCallResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.domain.model.City
import com.example.weatherapp.domain.model.Forecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun WeatherResponse.toCityEntity(isFavorite: Boolean = false): CityEntity {
    return CityEntity(
        cityId = cityId,
        cityName = cityName,
        country = sys.country,
        latitude = coordinates.latitude,
        longitude = coordinates.longitude,
        temperature = main.temperature,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        weatherMain = weather.firstOrNull()?.main ?: "",
        weatherDescription = weather.firstOrNull()?.description ?: "",
        weatherIcon = weather.firstOrNull()?.icon ?: "",
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        windDegree = wind.degree,
        cloudiness = clouds.cloudiness,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        timestamp = timestamp,
        isFavorite = isFavorite,
        lastUpdated = System.currentTimeMillis()
    )
}

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

// ForecastItem -> ForecastEntity
fun ForecastItem.toForecastEntity(cityId: Int): ForecastEntity {
    return ForecastEntity(
        cityId = cityId,
        timestamp = timestamp,
        temperature = main.temperature,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        weatherMain = weather.firstOrNull()?.main ?: "",
        weatherDescription = weather.firstOrNull()?.description ?: "",
        weatherIcon = weather.firstOrNull()?.icon ?: "",
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        windDegree = wind.degree,
        cloudiness = clouds.cloudiness,
        dateText = dateText,
        lastUpdated = System.currentTimeMillis()
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

fun OneCallResponse.toCityEntity(
    geocodingData: GeocodingResponse,
    isFavorite: Boolean = false,
    existingCityId: Int? = null
): CityEntity {
    val current = current
    // Use existing cityId if provided (for refresh), otherwise generate new one
    val cityId = existingCityId ?: generateCityId(geocodingData.latitude, geocodingData.longitude)

    return CityEntity(
        cityId = cityId,
        cityName = geocodingData.name,
        country = geocodingData.country,
        latitude = latitude,
        longitude = longitude,
        temperature = current.temperature,
        feelsLike = current.feelsLike,
        tempMin = current.temperature, // One Call current doesn't have min/max, use current temp
        tempMax = current.temperature,
        weatherMain = current.weather.firstOrNull()?.main ?: "",
        weatherDescription = current.weather.firstOrNull()?.description ?: "",
        weatherIcon = current.weather.firstOrNull()?.icon ?: "",
        humidity = current.humidity,
        pressure = current.pressure,
        windSpeed = current.windSpeed,
        windDegree = current.windDeg,
        cloudiness = current.clouds,
        sunrise = current.sunrise ?: 0L,
        sunset = current.sunset ?: 0L,
        timestamp = current.timestamp,
        isFavorite = isFavorite,
        lastUpdated = System.currentTimeMillis()
    )
}


fun OneCallHourly.toForecastEntity(cityId: Int): ForecastEntity {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateText = dateFormat.format(Date(timestamp * 1000))

    return ForecastEntity(
        cityId = cityId,
        timestamp = timestamp,
        temperature = temperature,
        feelsLike = feelsLike,
        tempMin = temperature, // Hourly doesn't have min/max
        tempMax = temperature,
        weatherMain = weather.firstOrNull()?.main ?: "",
        weatherDescription = weather.firstOrNull()?.description ?: "",
        weatherIcon = weather.firstOrNull()?.icon ?: "",
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        windDegree = windDeg,
        cloudiness = clouds,
        dateText = dateText,
        lastUpdated = System.currentTimeMillis()
    )
}



fun OneCallDaily.toForecastEntity(cityId: Int): ForecastEntity {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateText = dateFormat.format(Date(timestamp * 1000))

    return ForecastEntity(
        cityId = cityId,
        timestamp = timestamp,
        temperature = temp.day,
        feelsLike = feelsLike?.day ?: temp.day,
        tempMin = temp.min,
        tempMax = temp.max,
        weatherMain = weather.firstOrNull()?.main ?: "",
        weatherDescription = weather.firstOrNull()?.description ?: "",
        weatherIcon = weather.firstOrNull()?.icon ?: "",
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        windDegree = windDeg,
        cloudiness = clouds,
        dateText = dateText,
        lastUpdated = System.currentTimeMillis()
    )
}
