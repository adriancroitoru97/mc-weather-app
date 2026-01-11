package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.local.dao.CityDao
import com.example.weatherapp.data.local.dao.ForecastDao
import com.example.weatherapp.data.mapper.generateCityId
import com.example.weatherapp.data.mapper.toCity
import com.example.weatherapp.data.mapper.toCityEntity
import com.example.weatherapp.data.mapper.toForecast
import com.example.weatherapp.data.mapper.toForecastEntity
import com.example.weatherapp.data.remote.ApiConfig
import com.example.weatherapp.data.remote.GeocodingApiService
import com.example.weatherapp.data.remote.WeatherApiService
import com.example.weatherapp.domain.model.City
import com.example.weatherapp.domain.model.Forecast
import com.example.weatherapp.domain.model.Result
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class WeatherRepositoryImpl(
    private val cityDao: CityDao,
    private val forecastDao: ForecastDao,
    private val apiService: WeatherApiService = ApiConfig.weatherApiService,
    private val geocodingService: GeocodingApiService = ApiConfig.geocodingApiService,
    private val apiKey: String = ApiConfig.API_KEY
) : WeatherRepository {

    companion object {
        private const val TAG = "WeatherRepository"
    }

    override fun getAllCities(): Flow<List<City>> {
        return cityDao.getAllCities().map { entities ->
            entities.map { it.toCity() }
        }
    }

    override fun getFavoriteCities(): Flow<List<City>> {
        return cityDao.getFavoriteCities().map { entities ->
            entities.map { it.toCity() }
        }
    }

    override fun getCityById(cityId: Int): Flow<City?> {
        return cityDao.getCityByIdFlow(cityId).map { entity ->
            entity?.toCity()
        }
    }

    override suspend fun refreshWeatherForCity(cityId: Int): Result<City> {
        return try {
            val existingCity = cityDao.getCityById(cityId)
            if (existingCity == null) {
                return Result.Error(Exception("City not found"))
            }

            val isFavorite = existingCity.isFavorite

            // Use One Call API 3.0 with stored coordinates
            val response = apiService.getOneCallWeather(
                latitude = existingCity.latitude,
                longitude = existingCity.longitude,
                apiKey = apiKey
            )

            if (response.isSuccessful && response.body() != null) {
                val oneCallData = response.body()!!

                // Create geocoding-like data from existing city
                val geocodingData = com.example.weatherapp.data.model.GeocodingResponse(
                    name = existingCity.cityName,
                    latitude = existingCity.latitude,
                    longitude = existingCity.longitude,
                    country = existingCity.country
                )

                // When refreshing, always use the existing cityId to prevent duplicates
                val cityEntity = oneCallData.toCityEntity(
                    geocodingData,
                    isFavorite,
                    existingCityId = existingCity.cityId
                )
                cityDao.insertCity(cityEntity)

                // Also save hourly forecasts if available
                val hourlyForecasts = oneCallData.hourly?.take(24)?.map {
                    it.toForecastEntity(existingCity.cityId)
                } ?: emptyList()
                if (hourlyForecasts.isNotEmpty()) {
                    forecastDao.insertForecasts(hourlyForecasts)
                }

                Log.d(TAG, "Weather refreshed for city: ${existingCity.cityName}")
                Result.Success(cityEntity.toCity())
            } else {
                val errorMsg = "Failed to fetch weather: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.Error(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error refreshing weather", e)
            Result.Error(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing weather", e)
            Result.Error(e)
        }
    }

    override suspend fun addCityByName(cityName: String): Result<City> {
        return try {
            // Step 1: Get coordinates from Geocoding API
            val geocodingResponse = geocodingService.getCoordinatesByCityName(cityName, 1, apiKey)

            if (!geocodingResponse.isSuccessful || geocodingResponse.body().isNullOrEmpty()) {
                val errorMsg = "City not found: $cityName"
                Log.e(TAG, errorMsg)
                return Result.Error(Exception(errorMsg))
            }

            val geocodingData = geocodingResponse.body()!![0]

            // Step 2: Use One Call API 3.0 with coordinates
            val oneCallResponse = apiService.getOneCallWeather(
                latitude = geocodingData.latitude,
                longitude = geocodingData.longitude,
                apiKey = apiKey
            )

            if (!oneCallResponse.isSuccessful || oneCallResponse.body() == null) {
                val errorMsg = "Failed to fetch weather: ${oneCallResponse.code()}"
                Log.e(TAG, errorMsg)
                return Result.Error(Exception(errorMsg))
            }

            val oneCallData = oneCallResponse.body()!!

            // Check if city already exists by coordinates (to prevent duplicates)
            val existingCity = cityDao.findCityByCoordinates(
                geocodingData.latitude,
                geocodingData.longitude
            )

            // Use existing cityId if found, otherwise generate new one
            val cityId = existingCity?.cityId ?: generateCityId(
                geocodingData.latitude,
                geocodingData.longitude
            )
            val isFavoriteToUse = existingCity?.isFavorite ?: false

            val cityEntity = oneCallData.toCityEntity(
                geocodingData,
                isFavoriteToUse,
                existingCityId = cityId
            )
            cityDao.insertCity(cityEntity)

            // Save hourly forecasts if available
            val hourlyForecasts = oneCallData.hourly?.take(24)?.map {
                it.toForecastEntity(cityId)
            } ?: emptyList()
            if (hourlyForecasts.isNotEmpty()) {
                forecastDao.insertForecasts(hourlyForecasts)
            }

            Log.d(TAG, "City added: ${geocodingData.name}")
            Result.Success(cityEntity.toCity())
        } catch (e: IOException) {
            Log.e(TAG, "Network error adding city", e)
            Result.Error(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error adding city", e)
            Result.Error(e)
        }
    }

    override suspend fun addCityByCoordinates(latitude: Double, longitude: Double): Result<City> {
        return try {
            // Step 1: Get city name from reverse geocoding
            val reverseGeocodingResponse = geocodingService.getCityNameByCoordinates(
                latitude = latitude,
                longitude = longitude,
                limit = 1,
                apiKey = apiKey
            )

            // Step 2: Use One Call API 3.0
            val oneCallResponse = apiService.getOneCallWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )

            if (!oneCallResponse.isSuccessful || oneCallResponse.body() == null) {
                val errorMsg = "Failed to fetch weather by coordinates: ${oneCallResponse.code()}"
                Log.e(TAG, errorMsg)
                return Result.Error(Exception(errorMsg))
            }

            val oneCallData = oneCallResponse.body()!!

            // Create geocoding data - try to get from reverse geocoding if available
            val geocodingData = if (reverseGeocodingResponse.isSuccessful &&
                !reverseGeocodingResponse.body().isNullOrEmpty()
            ) {
                reverseGeocodingResponse.body()!![0]
            } else {
                // Fallback: use coordinates as name
                com.example.weatherapp.data.model.GeocodingResponse(
                    name = "Location (${String.format("%.2f", latitude)}, ${
                        String.format(
                            "%.2f",
                            longitude
                        )
                    })",
                    latitude = latitude,
                    longitude = longitude,
                    country = ""
                )
            }

            // Check if city already exists by coordinates (to prevent duplicates)
            val existingCity = cityDao.findCityByCoordinates(latitude, longitude)

            // Use existing cityId if found, otherwise generate new one
            val cityId = existingCity?.cityId ?: generateCityId(latitude, longitude)
            val isFavoriteToUse = existingCity?.isFavorite ?: false

            val cityEntity = oneCallData.toCityEntity(
                geocodingData,
                isFavoriteToUse,
                existingCityId = cityId
            )
            cityDao.insertCity(cityEntity)

            // Save hourly forecasts if available
            val hourlyForecasts = oneCallData.hourly?.take(24)?.map {
                it.toForecastEntity(cityId)
            } ?: emptyList()
            if (hourlyForecasts.isNotEmpty()) {
                forecastDao.insertForecasts(hourlyForecasts)
            }

            Log.d(TAG, "City added by coordinates: ${geocodingData.name}")
            Result.Success(cityEntity.toCity())
        } catch (e: IOException) {
            Log.e(TAG, "Network error adding city by coordinates", e)
            Result.Error(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error adding city by coordinates", e)
            Result.Error(e)
        }
    }

    override suspend fun updateFavoriteStatus(cityId: Int, isFavorite: Boolean) {
        cityDao.updateFavoriteStatus(cityId, isFavorite)
        Log.d(TAG, "Favorite status updated for city $cityId: $isFavorite")
    }

    override suspend fun deleteCity(cityId: Int) {
        cityDao.deleteCityById(cityId)
        forecastDao.deleteForecastsForCity(cityId)
        Log.d(TAG, "City deleted: $cityId")
    }

    override fun getForecastsForCity(cityId: Int): Flow<List<Forecast>> {
        return forecastDao.getForecastsForCity(cityId).map { entities ->
            entities.map { it.toForecast() }
        }
    }

    override suspend fun refreshForecastForCity(cityId: Int): Result<List<Forecast>> {
        return try {
            val existingCity = cityDao.getCityById(cityId)
            if (existingCity == null) {
                return Result.Error(Exception("City not found"))
            }

            // Use One Call API 3.0 to get forecasts
            val response = apiService.getOneCallWeather(
                latitude = existingCity.latitude,
                longitude = existingCity.longitude,
                apiKey = apiKey
            )

            if (response.isSuccessful && response.body() != null) {
                val oneCallData = response.body()!!

                // Delete old forecasts
                forecastDao.deleteForecastsForCity(cityId)

                // Insert hourly forecasts (next 24 hours)
                val hourlyForecasts = oneCallData.hourly?.take(24)?.map {
                    it.toForecastEntity(cityId)
                } ?: emptyList()

                // Insert daily forecasts (next 8 days)
                val dailyForecasts = oneCallData.daily?.take(8)?.map {
                    it.toForecastEntity(cityId)
                } ?: emptyList()

                val allForecasts = hourlyForecasts + dailyForecasts
                forecastDao.insertForecasts(allForecasts)

                Log.d(TAG, "Forecast refreshed for city $cityId: ${allForecasts.size} items")
                Result.Success(allForecasts.map { it.toForecast() })
            } else {
                val errorMsg = "Failed to fetch forecast: ${response.code()}"
                Log.e(TAG, errorMsg)
                Result.Error(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error refreshing forecast", e)
            Result.Error(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing forecast", e)
            Result.Error(e)
        }
    }

    override suspend fun refreshAllCities(): Result<Unit> {
        return try {
            val cities = cityDao.getAllCities()
            cities.collect { cityList ->
                cityList.forEach { city ->
                    refreshWeatherForCity(city.cityId)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing all cities", e)
            Result.Error(e)
        }
    }
}
