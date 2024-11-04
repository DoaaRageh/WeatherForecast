package com.example.weatherforecast.alert.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.model.AlarmRoom

class AlertDiffUtil: DiffUtil.ItemCallback<AlarmRoom>() {
    override fun areItemsTheSame(oldItem: AlarmRoom, newItem: AlarmRoom): Boolean {
        return oldItem.timeInMillis == newItem.timeInMillis
    }

    override fun areContentsTheSame(oldItem: AlarmRoom, newItem: AlarmRoom): Boolean {
        return oldItem == newItem
    }
}