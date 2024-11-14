package com.example.weatherforecast.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.viewmodel.AlertViewModel
import com.example.weatherforecast.db.AppDataBase
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.model.WeatherRepository

import com.example.weatherforecast.network.WeatherRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WeatherNotificationReceiver : BroadcastReceiver() {
    private var longitude:Double=0.0
    private var latitude:Double=0.0
    private var unit:String=""
    private var language:String=""
    private lateinit var actualLang:String
    var apiKey:String="0f121088b1919d00bf3ffec84d4357f9"
    private var units:String=""
    private lateinit var description: String
    private lateinit var currentLocation:String
    private val job = Job() // Job to manage the coroutine lifecycle
    private val scope = CoroutineScope(Dispatchers.IO + job)
    lateinit var repository: WeatherRepository

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            latitude = intent.getDoubleExtra("lat", 0.0)
            longitude = intent.getDoubleExtra("long", 0.0)
            unit= intent.getStringExtra("unit").toString()
            language= intent.getStringExtra("lang").toString()
        }
        Log.d("receiver", "onReceive: lat: $latitude ")
        checkOnSettings()

        repository = WeatherRepository.getInstance(

            WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(AppDataBase.getInstance(context).getForecastDao()))



        val viewModel = AlertViewModel(repository)
        scope.launch {
            viewModel.getWeather(longitude, latitude, apiKey, units, actualLang)
                viewModel.weatherList.collect{ weatherResponse->
                    weatherResponse?.let {
                        description= weatherResponse.weather?.get(0)?.description.toString()
                        currentLocation=weatherResponse.name.toString()

                        val notificationIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        context?.let {

                            val pastWeatherSummary = description


                            val notification = NotificationCompat.Builder(it, "weather_channel")
                                .setSmallIcon(R.drawable.baseline_cloud_queue_24)
                                .setContentTitle("Weather Alert")
                                .setContentText("Weather at $currentLocation: $description")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .build()

                            if (ActivityCompat.checkSelfPermission(
                                    it,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                NotificationManagerCompat.from(it).notify(1, notification)
                            }
                        }
                }
            }
        }


    }
    private fun checkOnSettings() {
        actualLang = if (language == "Arabic") "ar" else "en"
        units = when (unit) {
            "°C" -> "metric"
            "°F" -> "imperial"
            else -> "standard"
        }
    }

    // Cancel the job when done to avoid memory leaks
    fun cancel() {
        job.cancel()
    }
}