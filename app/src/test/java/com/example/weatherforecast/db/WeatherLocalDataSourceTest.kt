package com.example.weatherforecast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherLocalDataSourceTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var db: AppDataBase
    lateinit var weatherLocalDataSource: WeatherLocalDataSource

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDataBase::class.java).allowMainThreadQueries().build()

        weatherLocalDataSource = WeatherLocalDataSource(db.getForecastDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertLocation_insertsLocationCorrectly() = runTest {
        val location = Forcast( "Cairo", 30.0, 31.0, 1)
        weatherLocalDataSource.insertForecast(location)

        val firstlocation = weatherLocalDataSource.getAllForecast().first()

        assertThat(firstlocation, IsEqual(location))
    }

    @Test
    fun deletePlaceFromFav_deletesLocationCorrectly() = runTest {
        val location = Forcast( "Vienna", 35.0, 34.0, 1)
        weatherLocalDataSource.insertForecast(location)
        weatherLocalDataSource.deleteForecast(location)

        val allLocations = weatherLocalDataSource.getAllForecast()
        assertThat(allLocations.size, IsEqual(0))
    }

}