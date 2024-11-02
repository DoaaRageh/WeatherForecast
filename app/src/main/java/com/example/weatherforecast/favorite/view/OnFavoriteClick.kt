package com.example.weatherforecast.favorite.view

import com.example.weatherforecast.model.Forcast

interface OnFavoriteClick {
    suspend fun onRemoveClick(forecast: Forcast)
    suspend fun onFacClick(forecast: Forcast)
}