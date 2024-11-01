package com.example.weatherforecast.network


sealed class ApiState {
    class Success(val weather: Any): ApiState()
    class Failure(val msg: Throwable): ApiState()
    object Loading: ApiState()
}