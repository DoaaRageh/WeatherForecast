package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class AlarmRoom (val formattedTime:String, @PrimaryKey val timeInMillis:Long)