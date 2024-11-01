package com.example.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.Weather

@Database(entities = arrayOf(Weather::class), version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getProductDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val temp = Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "productsdb").build()
                INSTANCE = temp
                temp
            }
        }
    }
}