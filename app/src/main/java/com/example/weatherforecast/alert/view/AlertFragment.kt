package com.example.weatherforecast.alert.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.viewmodel.AlertViewModel
import com.example.weatherforecast.alert.viewmodel.AlertViewModelFactory
import com.example.weatherforecast.db.AppDataBase
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.model.AlarmRoom
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.WeatherRemoteDataSource
import com.example.weatherforecast.notification.WeatherNotificationReceiver
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar


class AlertFragment : Fragment(), onAlertClickListener {

    private lateinit var alertRecycler: RecyclerView
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var addAlarm: ImageView
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertLayoutManager: LinearLayoutManager
    private var long: Double = 0.0
    private var lat: Double = 0.0
    private var tempUnit: String = ""
    private var language: String = ""
    lateinit var repository: WeatherRepository

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alert, container, false)


        addAlarm = view.findViewById(R.id.addalarm)

        repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource.getInstance(AppDataBase.getInstance(requireContext()).getForecastDao())
        )
        val alertViewModelFactory = AlertViewModelFactory(repository)
        alertViewModel = ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        alertAdapter = AlertAdapter(this)
        alertLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        alertRecycler = view.findViewById(R.id.alertrv)
        alertRecycler.apply {
            adapter = alertAdapter
            layoutManager = alertLayoutManager
        }

        val alertSharedPre: SharedPreferences =
            requireContext().getSharedPreferences("alertsSharedPref", Context.MODE_PRIVATE)
        long = alertSharedPre.getLong("long", 0)?.toDouble()!!
        lat = alertSharedPre.getLong("lat", 0)?.toDouble()!!
        Log.d("AlertFragment", "alert sh pref : long $long")

        val settingSharedPref: SharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        tempUnit = settingSharedPref.getString("temperature", "°C").toString()
        language = settingSharedPref.getString("language", "en") ?: "en"

        addAlarm.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    scheduleWeatherAlarm(requireContext(), calendar.timeInMillis)

                    val alarm = AlarmRoom(
                        timeInMillis = calendar.timeInMillis,
                        formattedTime = calendar.time.toString()
                    )
                    alertViewModel.addAlarm(alarm)

                    Toast.makeText(requireContext(), "Alarm set for: ${calendar.time}", Toast.LENGTH_SHORT).show()
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        createNotificationChannel()

        lifecycleScope.launchWhenStarted {
            alertViewModel.alarmList.collectLatest { alarmList ->
                alertAdapter.submitList(alarmList)
            }
        }

        alertViewModel.getAllAlarm()

        return view
    }

    private fun scheduleWeatherAlarm(context: Context, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WeatherNotificationReceiver::class.java).apply {
            putExtra("long", long)
            putExtra("lat", lat)
            putExtra("unit", tempUnit)
            putExtra("lang", language)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Channel"
            val descriptionText = "Notifications for weather alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("weather_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRemoveClickListner(alarmRoom: AlarmRoom) {
        alertViewModel.deleteAlarm(alarmRoom)
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            AlertFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

