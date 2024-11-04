package com.example.weatherforecast.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.WeatherRepository

class AlertViewModelFactory (private val repo: WeatherRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(repo) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
