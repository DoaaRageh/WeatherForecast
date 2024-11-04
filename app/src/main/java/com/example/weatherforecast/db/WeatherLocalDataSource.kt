package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.AlarmRoom
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Weather
import kotlinx.coroutines.flow.Flow

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
    private val alertDao = AppDataBase.getInstance(context).getAlertDao()

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

    suspend fun insertAlarm(alarmRoom: AlarmRoom) {
        alertDao.addAlarm(alarmRoom)
    }

    suspend fun deleteAlarm(alarmRoom: AlarmRoom) {
        alertDao.deleteAlarm(alarmRoom)
    }

    fun getAllAlarms(): Flow<List<AlarmRoom>> {
        return alertDao.getAllAlarms()
    }


}