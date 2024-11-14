package com.example.weatherforecast.network

import com.example.weatherforecast.db.IWeatherDataSource
import com.example.weatherforecast.model.AlarmRoom
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


class WeatherRemoteDataSource: IWeatherDataSource {

    val apiInstance = RetrofitHelper.retrofitInstance.create(WetherService::class.java)

    /*suspend fun getProducts(): Response<WeatherResponse> {
        return apiInstance.getProducts()
    }*/

    override suspend fun getWeather(lat: Double, lon: Double, lang: String): Response<WeatherResponse> {
        return apiInstance.getWeather(lat, lon, lang)
    }

    override suspend fun getForecast(lat: Double, lon: Double, lang: String): Response<Forecast> {
        return apiInstance.getForecast(lat, lon, lang)
    }

    override suspend fun insertAlarm(alarmRoom: AlarmRoom) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarmRoom: AlarmRoom) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<AlarmRoom>> {
        TODO("Not yet implemented")
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRemoteDataSource? = null

        fun getInstance(): WeatherRemoteDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRemoteDataSource()
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun insertForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllForecast(): List<Forcast> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }

    override suspend fun updateForecast(forecast: Forcast) {
        TODO("Not yet implemented")
    }
}