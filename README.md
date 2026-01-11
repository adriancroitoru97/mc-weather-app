Weather App - Android Kotlin & Jetpack Compose

A modern Android weather application built with Kotlin, Jetpack Compose, and following MVVM architecture. The app displays weather information for cities with offline support and an intuitive UI.

Functionality:
- City List Screen: Display all added cities with current weather information
- City Detail Screen: Show comprehensive weather details and 5-day forecast
- Offline Support: Local Room database caching for offline access
- Real-time Weather: Integration with OpenWeatherMap API via Retrofit
- Location-based: Add cities by current GPS location
- Favorites: Mark cities as favorites for quick access
- Search: Filter cities by name or country
- Pull to Refresh: Update weather data for all cities

Screens

1. City List Screen
- Displays all added cities
- Shows current temperature and weather
- Search functionality
- Add city by name or location
- Pull-to-refresh
- Favorite/delete cities via menu

2. City Detail Screen
- Large weather display with gradient background
- Current conditions (temperature, feels like)
- Weather details grid (humidity, wind, pressure, cloudiness)
- 5-day forecast with daily cards
- Favorite toggle
- Pull-to-refresh

API Integration

The app uses the One Call 3.0 version of OpenWeatherMap API (https://openweathermap.org/api).

Authors

Croitoru Adrian-Valeriu - [https://github.com/adriancroitoru97]
Selea Tudor Octavian - [https://github.com/freakyBot12]