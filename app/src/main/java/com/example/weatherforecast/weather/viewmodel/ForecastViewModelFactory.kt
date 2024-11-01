package com.example.weatherforecast.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.WeatherRepository

class ForecastViewModelFactory(private val productRepo: WeatherRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
            return ForecastViewModel(productRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}