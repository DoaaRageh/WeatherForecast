package com.example.weatherforecast.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete // Correctly import the Delete annotation
import com.example.weatherforecast.model.Forcast

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: Forcast)

    @Query("SELECT * FROM forecast_table")
    suspend fun getAllForecast(): List<Forcast>

    @Delete // Use Room's Delete annotation
    suspend fun deleteForecast(forecast: Forcast)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateForecast(forecast: Forcast)
}
