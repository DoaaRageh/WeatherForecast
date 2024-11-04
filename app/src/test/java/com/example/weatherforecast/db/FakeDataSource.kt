package com.example.weatherforecast.db

import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.WeatherResponse
import retrofit2.Response

class FakeDataSource(private val forecastData: MutableList<Forcast>? = mutableListOf()): IWeatherDataSource {
    override suspend fun insertForecast(forecast: Forcast) {
        forecastData?.add(forecast)
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return forecastData ?: emptyList()
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        forecastData?.remove(forecast)
    }

    override suspend fun updateForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Response<Forecast> {
        TODO("Not yet implemented")
    }
}