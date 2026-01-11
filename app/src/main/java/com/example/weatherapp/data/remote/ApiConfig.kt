package com.example.weatherapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    const val API_KEY = "87466726ea3a67772b0a6ba24924b0f9"
    
    private const val ONE_CALL_BASE_URL = "https://api.openweathermap.org/data/3.0/"
    private const val GEOCODING_BASE_URL = "https://api.openweathermap.org/geo/1.0/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val oneCallRetrofit = Retrofit.Builder()
        .baseUrl(ONE_CALL_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val geocodingRetrofit = Retrofit.Builder()
        .baseUrl(GEOCODING_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApiService: WeatherApiService = oneCallRetrofit.create(WeatherApiService::class.java)
    val geocodingApiService: GeocodingApiService = geocodingRetrofit.create(GeocodingApiService::class.java)
}
