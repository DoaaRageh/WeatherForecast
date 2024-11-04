package com.example.weatherforecast.model

import android.util.Log
import com.example.weatherforecast.db.IWeatherDataSource
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository (private val remoteDataSource: IWeatherDataSource, private val localDataSource: IWeatherDataSource) :
    IWeatherRepository {

         /*suspend fun getAllProducts(): Response<WeatherResponse>  {
            return remoteDataSource.getProducts()
        }*/

    override suspend fun getWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherResponse> = flow {
        val response = remoteDataSource.getWeather(lat, lon, units, lang)
        if (response.isSuccessful && response.body() != null) {
            Log.i("TAG", "getWeather: response success ${response.body()?.main?.temp}")
            emit(response.body()!!)
        }
        else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            val statusCode = response.code()

            Log.e("TAG", "getWeather: response fail with status code $statusCode. Error: $errorMessage")
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double, units: String, lang: String): Flow<Forecast> = flow {
        val response = remoteDataSource.getForecast(lat, lon, units, lang)
        if (response.isSuccessful && response.body() != null) {
            Log.i("TAG", "getWeather: response success")
            emit(response.body()!!)
        }
        else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            val statusCode = response.code()

            Log.e("TAG", "getWeather: response fail with status code $statusCode. Error: $errorMessage")
        }
    }

    override suspend fun insertForecast(forecast: Forcast) {
        localDataSource.insertForecast(forecast)
    }

    override suspend fun getAllForecast(): List<Forcast> {
        return localDataSource.getAllForecast()
    }

    override suspend fun deleteForecast(forecast: Forcast) {
        localDataSource.deleteForecast(forecast)
    }

    override suspend fun updateForecast(forecast: Forcast) {
        localDataSource.updateForecast(forecast)
    }

    suspend fun addAlarm(alarmRoom: AlarmRoom) {
        localDataSource.insertAlarm(alarmRoom)
    }

    suspend fun removeAlarm(alarmRoom: AlarmRoom) {
        localDataSource.deleteAlarm(alarmRoom)
    }

    fun getAllAlarms(): Flow<List<AlarmRoom>> {
        return localDataSource.getAllAlarms()
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(remoteDataSource: WeatherRemoteDataSource, localDataSource: WeatherLocalDataSource): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepository(remoteDataSource, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
}