package com.example.weatherforecast.model

import android.util.Log
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.network.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherRepository (private val remoteDataSource: WeatherRemoteDataSource, private val localDataSource: WeatherLocalDataSource) {

         /*suspend fun getAllProducts(): Response<WeatherResponse>  {
            return remoteDataSource.getProducts()
        }*/

    suspend fun getWeather(lat: Double, lon: Double, units: String, lang: String): Flow<WeatherResponse> = flow {
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

    suspend fun getForecast(lat: Double, lon: Double, units: String, lang: String): Flow<Forecast> = flow {
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

        /* suspend fun insertProduct(weather: Weather) = withContext(Dispatchers.IO) {
            localDataSource.insertProduct(weather)
        }

         suspend fun deleteProduct(weather: Weather) = withContext(Dispatchers.IO) {
            localDataSource.removeProduct(weather)
        }*/

         /*suspend fun getStoredProducts(): List<Weather> {
            return localDataSource.getStoredProducts()
        }*/

    suspend fun insertForecast(forecast: Forcast) {
        localDataSource.insertForecast(forecast)
    }

    suspend fun getAllForecast(): List<Forcast> {
        return localDataSource.getAllForecast()
    }

    suspend fun deleteForecast(forecast: Forcast) {
        localDataSource.deleteForecast(forecast)
    }

    suspend fun updateForecast(forecast: Forcast) {
        localDataSource.updateForecast(forecast)
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