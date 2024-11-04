package com.example.weatherforecast.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.DailyWeather

class DailyDiffUtil: DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem == newItem
    }
}