package com.example.weatherforecast.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.model.Forcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ForecastDaoTest {
    lateinit var db: AppDataBase
    lateinit var forecastDao: ForecastDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDataBase::class.java).allowMainThreadQueries().build()

        forecastDao = db.getForecastDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertLocation_insertsLocation() = runTest {
        val location = Forcast( city= "Cairo", lat = 30.0, lon = 31.0, id = 1)
        forecastDao.insertForecast(location)

        val firstLocation = forecastDao.getAllForecast().first()

        assertThat(firstLocation, IsEqual(location))

    }

    @Test
    fun deleteLocation_removesLocation() = runTest {
        val location = Forcast( city= "Cairo", lat = 30.0, lon = 31.0, id = 1)
        forecastDao.insertForecast(location)

        forecastDao.deleteForecast(location)

        val allLocations = forecastDao.getAllForecast()
        assertThat(allLocations.size, IsEqual(0))
    }

}