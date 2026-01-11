package com.example.weatherapp.ui.citydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.model.City
import com.example.weatherapp.domain.model.Forecast
import com.example.weatherapp.domain.model.Result
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CityDetailUiState(
    val city: City? = null,
    val forecasts: List<Forecast> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class CityDetailViewModel(
    private val repository: WeatherRepository,
    private val cityId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityDetailUiState())
    val uiState: StateFlow<CityDetailUiState> = _uiState.asStateFlow()

    init {
        loadCityDetails()
        loadForecasts()
    }

    private fun loadCityDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getCityById(cityId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            isLoading = false
                        )
                    }
                }
                .collect { city ->
                    _uiState.update {
                        it.copy(
                            city = city,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    private fun loadForecasts() {
        viewModelScope.launch {
            repository.getForecastsForCity(cityId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { forecasts ->
                    _uiState.update { it.copy(forecasts = forecasts) }
                }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            when (repository.refreshWeatherForCity(cityId)) {
                is Result.Success -> {
                    // Success handled by Flow
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = "Failed to refresh weather"
                        )
                    }
                }

                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun refreshForecast() {
        viewModelScope.launch {
            when (repository.refreshForecastForCity(cityId)) {
                is Result.Success -> {
                    // Success handled by Flow
                }

                is Result.Error -> {
                    _uiState.update { it.copy(error = "Failed to refresh forecast") }
                }

                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun refreshAll() {
        refreshWeather()
        refreshForecast()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.city?.let { city ->
                repository.updateFavoriteStatus(cityId, !city.isFavorite)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
