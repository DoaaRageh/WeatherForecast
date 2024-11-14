package com.example.weatherforecast.db

import com.example.weatherforecast.model.AlarmRoom
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IWeatherDataSource {
    suspend fun insertForecast(forecast: Forcast)

    suspend fun getAllForecast(): List<Forcast>

    suspend fun deleteForecast(forecast: Forcast)

    suspend fun updateForecast(forecast: Forcast)

    suspend fun getWeather(lat: Double, lon: Double, lang: String): Response<WeatherResponse>

    suspend fun getForecast(lat: Double, lon: Double, lang: String): Response<Forecast>

    suspend fun insertAlarm(alarmRoom: AlarmRoom)

    suspend fun deleteAlarm(alarmRoom: AlarmRoom)

    fun getAllAlarms(): Flow<List<AlarmRoom>>
}