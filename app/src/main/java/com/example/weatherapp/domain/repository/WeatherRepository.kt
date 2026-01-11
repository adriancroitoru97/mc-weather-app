package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.City
import com.example.weatherapp.domain.model.Forecast
import com.example.weatherapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getAllCities(): Flow<List<City>>
    fun getFavoriteCities(): Flow<List<City>>
    fun getCityById(cityId: Int): Flow<City?>
    suspend fun refreshWeatherForCity(cityId: Int): Result<City>
    suspend fun addCityByName(cityName: String): Result<City>
    suspend fun addCityByCoordinates(latitude: Double, longitude: Double): Result<City>
    suspend fun updateFavoriteStatus(cityId: Int, isFavorite: Boolean)
    suspend fun deleteCity(cityId: Int)

    fun getForecastsForCity(cityId: Int): Flow<List<Forecast>>
    suspend fun refreshForecastForCity(cityId: Int): Result<List<Forecast>>

    suspend fun refreshAllCities(): Result<Unit>
}