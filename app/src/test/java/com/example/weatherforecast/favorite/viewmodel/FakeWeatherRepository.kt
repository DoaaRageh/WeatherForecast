package com.example.weatherforecast.favorite.viewmodel

import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.IWeatherRepository
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeWeatherRepository: IWeatherRepository {
    private val forecastList = mutableListOf<Forcast>()
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Forecast> {
        TODO("Not yet implemented")
    }

    override suspend fun insertForecast(forecast: Forcast) {
        forecastList.add(forecast)
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return forecastList
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        forecastList.remove(forecast)
    }

    override suspend fun updateForecast(forecast: Forcast) {
        val index = forecastList.indexOfFirst { it.id == forecast.id }
        if (index != -1) {
            forecastList[index] = forecast
        }
    }
}