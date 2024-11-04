package com.example.weatherforecast.alert.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.AlarmRoom
import com.example.weatherforecast.model.WeatherRepository

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val repo: WeatherRepository) : ViewModel() {

    private var _alarmList = MutableStateFlow<List<AlarmRoom>>(emptyList())
    val alarmList: StateFlow<List<AlarmRoom>> = _alarmList.asStateFlow()

    val _weatherList = MutableStateFlow<WeatherResponse?>(null)
    val weatherList: StateFlow<WeatherResponse?> = _weatherList

    fun getAllAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlarms().collect { list ->
                _alarmList.value = list
            }
        }
    }

    fun addAlarm(alarmRoom: AlarmRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addAlarm(alarmRoom)

            getAllAlarm()
        }
    }

    fun deleteAlarm(alarmRoom: AlarmRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeAlarm(alarmRoom)
        }
    }

    fun getWeather(long: Double, lat: Double, appid: String, units: String,lang:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weather = repo.getWeather(long, lat, units,lang).collect { weatherResponse ->
                _weatherList.value = weatherResponse
                Log.d("favvm", "Weather: ${weatherResponse}")

            }

        }
    }

}



