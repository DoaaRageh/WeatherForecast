package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse>

    suspend fun getForecast(lat: Double, lon: Double, units: String, lang: String): Flow<Forecast>

    suspend fun insertForecast(forecast: Forcast)

    suspend fun getAllForecast(): List<Forcast>

    suspend fun deleteForecast(forecast: Forcast)

    suspend fun updateForecast(forecast: Forcast)
}