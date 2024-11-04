package com.example.weatherforecast.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.IWeatherRepository
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: IWeatherRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Forcast>>(emptyList())
    val favorites: StateFlow<List<Forcast>> get() = _favorites

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = repository.getAllForecast()
        }
    }

    private fun addFavorite(forecast: Forcast) {
        viewModelScope.launch {
            repository.insertForecast(forecast)
            loadFavorites()
        }
    }

    private fun removeFavorite(forecast: Forcast) {
        viewModelScope.launch {
            repository.deleteForecast(forecast)
            loadFavorites()
        }
    }
    private fun isFavorite(forecast: Forcast): Boolean {
        return _favorites.value.contains(forecast)
    }
    fun updateFavoriteStatus(forecast: Forcast) {
        if (isFavorite(forecast)) {
            removeFavorite(forecast)
        } else {
            addFavorite(forecast)
        }
    }
    /*fun getFavoriteById(id: Int): Forcast? {
        return _favorites.value.find { it.list[0].weather[0].id.toInt() == id }
    }

    fun getFavoriteByCity(city: String): Forcast? {
        return _favorites.value.find { it.city == city }
    }*/
}
