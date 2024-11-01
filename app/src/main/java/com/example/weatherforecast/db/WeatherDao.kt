package com.example.weatherforecast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.Weather

@Dao
interface WeatherDao {
    /*@Query("SELECT * FROM product")
    suspend fun getAllProducts(): List<Weather>

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getCount(): Int*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(weather: Weather): Long

    @Delete
    suspend fun deleteProduct(weather: Weather): Int


}