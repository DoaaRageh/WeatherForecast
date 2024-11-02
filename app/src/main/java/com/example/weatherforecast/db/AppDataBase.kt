package com.example.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.Weather

@Database(entities = arrayOf(Forcast::class), version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getForecastDao(): ForecastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val temp = Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "forecastdb").build()
                INSTANCE = temp
                temp
            }
        }
    }
}