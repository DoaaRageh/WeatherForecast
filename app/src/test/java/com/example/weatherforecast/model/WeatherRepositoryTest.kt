package com.example.weatherforecast.model

import com.example.weatherforecast.db.FakeDataSource
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Test

class WeatherRepositoryTest {

    val location1 = Forcast("egypt" , 30.0 , 31.0, 1)
    val location2 = Forcast("veinna" , 35.0 , 34.0, 2)
    val location3 = Forcast("egypt" , 31.0 , 32.0, 1)
    val location4 = Forcast("veinna" , 33.0 , 33.0, 2)

    val remoteTasks = listOf(location1, location2)
    val localTasks = listOf(location3, location4)

    val fakeLocalDataSource = FakeDataSource(localTasks.toMutableList())
    val fakeRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())

    val repo = WeatherRepository(fakeRemoteDataSource, fakeLocalDataSource)

    @Test
    fun insertPlaceToFav_addsLocation() = runTest{
        //Given
        val newLocation = Forcast( "New Location", 25.0, 30.0, 3)
        repo.insertForecast(newLocation)

        val allLocations = repo.getAllForecast()
        assertThat(allLocations.size, IsEqual(3))
    }

    @Test
    fun deletePlaceFromFav_removesLocation() = runTest {
        repo.deleteForecast(location3)

        val allLocations = repo.getAllForecast()
        assertThat(allLocations.size, IsEqual(1))
        assertThat(allLocations.contains(location3), IsEqual(false))
    }

    /*@Test
    fun fetchCurrentWeather_weatherdata() = runTest {

        // Collect the first emission from the flow
        val emittedWeather = repo.getWeather(30.0, 31.0, "metric", "en")
        // Expected data from FakeRemote
        val expectedWeather = fakeRemoteDataSource.weatherTestData
        // Assertion
        assertThat(emittedWeather, equalTo(expectedWeather))

    }*/


    // Local
    /*@Test
    fun insertPlaceToFav_addsLocation() = runTest {
        val newLocation = Forcast( "New Location", 25.0, 30.0)
        repository.insertPlaceToFav(newLocation)

        val allLocations = repository.getAllFavouritePlaces().first()
        assertThat(allLocations.size, IsEqual(3)) // Assuming there were initially 2 locations
        assertThat(allLocations.contains(newLocation), IsEqual(true))
    }*/
}