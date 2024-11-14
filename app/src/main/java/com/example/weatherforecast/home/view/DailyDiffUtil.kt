package com.example.weatherforecast.weather.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.DailyWeather
import com.example.weatherforecast.model.ForecastElement

class DailyDiffUtil: DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem == newItem
    }
}