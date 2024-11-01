package com.example.weatherforecast.network

import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.model.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WetherService {
    @GET("forecast")
    suspend fun getForecast(): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getForecast(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("units") units: String,
                           @Query("lang") lang: String, @Query("appid") appId: String = "ad34eb3b0828e3710f5503b35fc9d23d"): Response<Forecast>

    @GET("weather")
    suspend fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("units") units: String,
    @Query("lang") lang: String, @Query("appid") appId: String = "ad34eb3b0828e3710f5503b35fc9d23d"): Response<WeatherResponse>
}

object RetrofitHelper {
    private const val BaseUrl: String = "https://api.openweathermap.org/data/2.5/"
    val retrofitInstance = Retrofit.Builder()
        .baseUrl(BaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}