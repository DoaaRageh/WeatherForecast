package com.example.weatherforecast.weather.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.ForecastElement

class WeatherDiffUtil: DiffUtil.ItemCallback<ForecastElement>() {
    override fun areItemsTheSame(oldItem: ForecastElement, newItem: ForecastElement): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: ForecastElement, newItem: ForecastElement): Boolean {
        return oldItem == newItem
    }
}