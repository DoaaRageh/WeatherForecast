package com.example.weatherforecast.network

import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.concurrent.Flow


class WeatherRemoteDataSource {

    val apiInstance = RetrofitHelper.retrofitInstance.create(WetherService::class.java)

    /*suspend fun getProducts(): Response<WeatherResponse> {
        return apiInstance.getProducts()
    }*/

     suspend fun getWeather(lat: Double, lon: Double, units: String, lang: String): Response<WeatherResponse> {
        return apiInstance.getWeather(lat, lon, units, lang)
    }

    suspend fun getForecast(lat: Double, lon: Double, units: String, lang: String): Response<Forecast> {
        return apiInstance.getForecast(lat, lon, units, lang)
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
}