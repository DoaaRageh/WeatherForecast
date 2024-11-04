package com.example.weatherforecast.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.IWeatherRepository
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ForecastViewModel(private val weatherRepo: IWeatherRepository) : ViewModel() {

    private val _apiState = MutableStateFlow<ApiState>(ApiState.Loading)
    val apiState: MutableStateFlow<ApiState> = _apiState

    var lastSuccessfulForecast: Forecast? = null


    fun getForecast(lat: Double, lon: Double, units: String, lang: String) {
        _apiState.value = ApiState.Loading
        viewModelScope.launch {
            weatherRepo.getForecast(lat, lon, units, lang)
                .catch { e ->
                    Log.i("TAG", "getWeather: $e")
                    _apiState.value = ApiState.Failure(e)
                }
                .collect { weather ->
                    Log.i("TAG", "getWeather: ")
                    lastSuccessfulForecast = weather
                    _apiState.value = ApiState.Success(weather)
                }
        }
    }

}