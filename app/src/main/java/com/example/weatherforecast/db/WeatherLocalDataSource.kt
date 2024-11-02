package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Weather

class WeatherLocalDataSource(private val context: Context) {


    //private val weatherDao: WeatherDao = AppDataBase.getInstance(context).getProductDao()


     /*suspend fun getStoredProducts(): List<Weather> {
        return weatherDao.getAllProducts()
    }*/

     /*suspend fun insertProduct(product: Weather) {
        weatherDao.insertProduct(product)
    }

     suspend fun removeProduct(product: Weather) {
        weatherDao.deleteProduct(product)
    }*/

    companion object {
        @Volatile
        private var INSTANCE: WeatherLocalDataSource? = null

        fun getInstance(context: Context): WeatherLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherLocalDataSource(context)
                INSTANCE = instance
                instance
            }
        }
    }

    private val forecastDao = AppDataBase.getInstance(context).getForecastDao()

    suspend fun insertForecast(forecast: Forcast) {
        forecastDao.insertForecast(forecast)
    }

    suspend fun getAllForecast(): List<Forcast> {
        return forecastDao.getAllForecast()
    }

    suspend fun deleteForecast(forecast: Forcast) {
        forecastDao.deleteForecast(forecast)
    }

    suspend fun updateForecast(forecast: Forcast) {
        forecastDao.updateForecast(forecast)
    }


}