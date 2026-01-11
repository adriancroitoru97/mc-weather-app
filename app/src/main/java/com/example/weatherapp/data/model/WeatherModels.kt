package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("coord")
    val coordinates: Coordinates,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("main")
    val main: MainWeatherData,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("timezone")
    val timezone: Int,
    @SerializedName("id")
    val cityId: Int,
    @SerializedName("name")
    val cityName: String
)

data class Coordinates(
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("lat")
    val latitude: Double
)

data class Weather(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

data class MainWeatherData(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("sea_level")
    val seaLevel: Int? = null,
    @SerializedName("grnd_level")
    val groundLevel: Int? = null
)

data class Wind(
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val degree: Int,
    @SerializedName("gust")
    val gust: Double? = null
)

data class Clouds(
    @SerializedName("all")
    val cloudiness: Int
)

data class Sys(
    @SerializedName("country")
    val country: String,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long
)

/**
 * Forecast API Response
 */
data class ForecastResponse(
    @SerializedName("list")
    val forecasts: List<ForecastItem>,
    @SerializedName("city")
    val city: City
)

data class ForecastItem(
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("main")
    val main: MainWeatherData,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("dt_txt")
    val dateText: String
)

data class City(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("coord")
    val coordinates: Coordinates,
    @SerializedName("country")
    val country: String,
    @SerializedName("timezone")
    val timezone: Int
)

data class GeocodingResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("country")
    val country: String,
    @SerializedName("state")
    val state: String? = null
)

data class OneCallResponse(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    @SerializedName("current")
    val current: OneCallCurrent,
    @SerializedName("hourly")
    val hourly: List<OneCallHourly>? = null,
    @SerializedName("daily")
    val daily: List<OneCallDaily>? = null
)

data class OneCallCurrent(
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("sunrise")
    val sunrise: Long? = null,
    @SerializedName("sunset")
    val sunset: Long? = null,
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double? = null,
    @SerializedName("uvi")
    val uvi: Double? = null,
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("visibility")
    val visibility: Int? = null,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double? = null,
    @SerializedName("weather")
    val weather: List<Weather>
)

data class OneCallHourly(
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double? = null,
    @SerializedName("uvi")
    val uvi: Double? = null,
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("visibility")
    val visibility: Int? = null,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double? = null,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("pop")
    val pop: Double? = null
)

data class OneCallDaily(
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long,
    @SerializedName("moonrise")
    val moonrise: Long? = null,
    @SerializedName("moonset")
    val moonset: Long? = null,
    @SerializedName("moon_phase")
    val moonPhase: Double? = null,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("temp")
    val temp: OneCallDailyTemp,
    @SerializedName("feels_like")
    val feelsLike: OneCallDailyFeelsLike? = null,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double? = null,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double? = null,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("pop")
    val pop: Double? = null,
    @SerializedName("uvi")
    val uvi: Double? = null
)

data class OneCallDailyTemp(
    @SerializedName("day")
    val day: Double,
    @SerializedName("min")
    val min: Double,
    @SerializedName("max")
    val max: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double
)

data class OneCallDailyFeelsLike(
    @SerializedName("day")
    val day: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double
)