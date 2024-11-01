package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "weather")
data class Weather(@PrimaryKey var id: String, var main: String, var description: String, var icon: String): Serializable

data class Clouds(var all: Int)

data class Wind(var deg: Long, var speed: Double, var gust: Double)

data class Main(var feels_like: Double, var humidity: Int, var pressure: Int, var temp: Double, var temp_max: Double, var temp_min: Double)

data class HourlyWeather(var dt: Long, var main: Main, var weather: List<Weather>, var wind: Wind, var clouds: Clouds,)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long,
    val id: Long,
    val type: Long
)

data class City(
    val country: String,
    val coord: Coord,
    val sunrise: Long,
    val timezone: Long,
    val sunset: Long,
    val name: String,
    val id: Long,
    val population: Long
)

data class Hourly(
    val city: City,
    val list: List<HourlyWeather>
)

data class DailyForecast(
    val list: List<ForecastElement>,
    val city: City
)

data class Forecast(
    val list: List<ForecastElement>,
    val city: City
)

data class ForecastElement(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String
)

data class WeatherResponse(
    val visibility: Long,
    val timezone: Long,
    val main: Main,
    val clouds: Clouds,
    val sys: Sys,
    val dt: Long,
    val coord: Coord,
    val weather: List<Weather>,
    val name: String,
    val cod: Long,
    val id: Long,
    val base: String,
    val wind: Wind
)
