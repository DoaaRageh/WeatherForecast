package com.example.weatherforecast.db

import com.example.weatherforecast.model.AlarmRoom
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class WeatherLocalDataSource(val forecastDao: ForecastDao) : IWeatherDataSource {


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

        fun getInstance(forecastDao: ForecastDao): WeatherLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherLocalDataSource(forecastDao)
                INSTANCE = instance
                instance
            }
        }
    }

    //private val forecastDao = AppDataBase.getInstance(context).getForecastDao()
    //private val alertDao = AppDataBase.getInstance(context).getAlertDao()

    override suspend fun insertForecast(forecast: Forcast) {
        forecastDao.insertForecast(forecast)
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return forecastDao.getAllForecast()
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        forecastDao.deleteForecast(forecast)
    }

    override suspend fun updateForecast(forecast: Forcast) {
        forecastDao.updateForecast(forecast)
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

    override suspend fun insertAlarm(alarmRoom: AlarmRoom) {
        forecastDao.addAlarm(alarmRoom)
    }

    override suspend fun deleteAlarm(alarmRoom: AlarmRoom) {
        forecastDao.deleteAlarm(alarmRoom)
    }

    override fun getAllAlarms(): Flow<List<AlarmRoom>> {
        return forecastDao.getAllAlarms()
    }


}