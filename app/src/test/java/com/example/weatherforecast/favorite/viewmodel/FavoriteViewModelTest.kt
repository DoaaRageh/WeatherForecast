package com.example.weatherforecast.favorite.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.OurNewRule
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.IWeatherRepository
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var repository: IWeatherRepository

    val location1 = Forcast("Cairo", 30.0, 31.0, 1)
    val location2 = Forcast("Roma", 32.0, 33.0, 2)

    @get:Rule
    val ourNewRule = OurNewRule()

    @Before
    fun setUp() {
        repository = FakeWeatherRepository()
        favoriteViewModel = FavoriteViewModel(repository)
    }

    @Test
    fun insertPlaceToFav_AllFavourite() = runTest {
        // When
        favoriteViewModel.updateFavoriteStatus(location1)

        // Use advanceUntilIdle() to make sure all background coroutines are finished
        advanceUntilIdle()

        // Collect the list of favorite locations from the StateFlow
        val favoritePlaces = favoriteViewModel.favorites.value  // Collect the first value from StateFlow

        // Check that the favorite list is not null or empty
        assertThat(favoritePlaces, not(nullValue()))

        // Check that the location was added correctly
        assertEquals(1, favoritePlaces.size)
        assertThat(favoritePlaces[0], `is`(location1))
    }

    @Test
    fun deleteFromFav() = runTest {
        // When adding a place to favorites
        favoriteViewModel.updateFavoriteStatus(location2)
        advanceUntilIdle()  // Ensure the coroutine is completed

        // Collect the list of favorite locations from the StateFlow
        val favoritePlace = favoriteViewModel.favorites.value        // Assert that the location was added
        assertEquals(1, favoritePlace.size)
        assertThat(favoritePlace[0], `is`(location2))

        // Now, delete it by calling updateFavoriteStatus again
        favoriteViewModel.updateFavoriteStatus(location2)
        advanceUntilIdle()  // Ensure the coroutine is completed

        // Collect the updated list of favorite locations
        val favorite = favoriteViewModel.favorites.value

        // Check that the location was removed (should be empty)
        assertThat(favorite, IsEqual(emptyList()))
    }

}
