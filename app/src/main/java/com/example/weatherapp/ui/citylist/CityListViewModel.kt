package com.example.weatherapp.ui.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.model.City
import com.example.weatherapp.domain.model.Result
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CityListUiState(
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class CityListViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityListUiState())
    val uiState: StateFlow<CityListUiState> = _uiState.asStateFlow()

    init {
        loadCities()
    }

    private fun loadCities() {
        viewModelScope.launch {
            repository.getAllCities()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { cities ->
                    _uiState.update {
                        it.copy(
                            cities = cities,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    fun addCityByName(cityName: String) {
        if (cityName.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a city name") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.addCityByName(cityName.trim())) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to add city"
                        )
                    }
                }

                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun addCityByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.addCityByCoordinates(latitude, longitude)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to add city"
                        )
                    }
                }

                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun refreshWeather(cityId: Int) {
        viewModelScope.launch {
            when (repository.refreshWeatherForCity(cityId)) {
                is Result.Success -> {
                    // Success handled by Flow
                }

                is Result.Error -> {
                    _uiState.update { it.copy(error = "Failed to refresh weather") }
                }

                is Result.Loading -> {
                    // Loading state
                }
            }
        }
    }

    fun refreshAllCities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            val cities = _uiState.value.cities
            cities.forEach { city ->
                repository.refreshWeatherForCity(city.cityId)
            }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun toggleFavorite(cityId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(cityId, isFavorite)
        }
    }

    fun deleteCity(cityId: Int) {
        viewModelScope.launch {
            repository.deleteCity(cityId)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun getFilteredCities(): List<City> {
        val query = _uiState.value.searchQuery.lowercase()
        return if (query.isEmpty()) {
            _uiState.value.cities
        } else {
            _uiState.value.cities.filter { city ->
                city.cityName.lowercase().contains(query) ||
                        city.country.lowercase().contains(query)
            }
        }
    }
}
