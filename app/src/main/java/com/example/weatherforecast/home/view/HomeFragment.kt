package com.example.weatherforecast.home.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.db.AppDataBase
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.model.DailyWeather
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.ApiState
import com.example.weatherforecast.network.WeatherRemoteDataSource
import com.example.weatherforecast.home.viewmodel.ForecastViewModel
import com.example.weatherforecast.home.viewmodel.ForecastViewModelFactory
import com.example.weatherforecast.home.viewmodel.WeatherViewModel
import com.example.weatherforecast.home.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var weatherFactory: WeatherViewModelFactory
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var forecastFactory: ForecastViewModelFactory
    private lateinit var forecastViewModel: ForecastViewModel
    lateinit var binding: FragmentHomeBinding
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var fusedLocationproviderClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    val iconsUrl = "https://openweathermap.org/img/wn/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")

        binding = FragmentHomeBinding.inflate(inflater, container, false )

        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        var language = prefs.getString("language", "en")
        prefs.edit().putString("fragment", "home").apply()



        hourlyAdapter = HourlyAdapter(requireContext()){}

        dailyAdapter = DailyAdapter(requireContext()){}

        binding.hourlyRecyclerView.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }

        binding.dailyRecyclerView.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        weatherFactory = WeatherViewModelFactory(
            WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(AppDataBase.getInstance(requireContext()).getForecastDao()))
        )

        weatherViewModel = ViewModelProvider(this, weatherFactory).get(WeatherViewModel::class.java)

        forecastFactory = ForecastViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(AppDataBase.getInstance(requireContext()).getForecastDao()))
        )

        forecastViewModel = ViewModelProvider(this, forecastFactory).get(ForecastViewModel::class.java)


        lifecycleScope.launch {
            weatherViewModel.apiState.collectLatest { state ->
                when(state) {
                    is ApiState.Loading -> {
                        Log.i("TAG", "onCreateView: Loading")
                        binding.progressBar.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    }
                    is ApiState.Success -> {
                        Log.i("TAG", "onCreateView: Success")
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE
                        var response = state.weather as WeatherResponse
                        //binding.tvTempreture.text = response.main.temp.toString()
                        binding.tvDescription.text = response.weather[0].description
                        binding.tvCity.text = response.name
                        //binding.windSpeed.text = response.wind.speed.toString()
                        binding.tvHumidity.text = response.main.humidity.toString() + "%"
                        binding.tvCloud.text = response.clouds.all.toString() + "%"
                        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                        val windSpeedUnit = prefs.getString("windSpeed", "meterPerSec")
                        val unit = prefs.getString("temperature", "k")
                        val tempInKelvin= response.main.temp
                        val windSpeed = response.wind.speed
                        when (unit) {
                            "c" -> {
                                // Convert Kelvin to Celsius
                                val tempInCelsius = tempInKelvin - 273.15
                                binding.tvTempreture.text = "%.1f".format(tempInCelsius)
                                binding.tvUnit.text = "°C"
                            }
                            "f" -> {
                                // Convert Kelvin to Fahrenheit
                                val tempInFahrenheit = (tempInKelvin - 273.15) * 9/5 + 32
                                binding.tvTempreture.text = "%.1f".format(tempInFahrenheit)
                                binding.tvUnit.text = "°F"
                            }
                            else -> {
                                // Default to Kelvin
                                binding.tvTempreture.text = "%.1f".format(tempInKelvin)
                                binding.tvUnit.text = "K"
                            }
                        }

                        val formattedWindSpeedText = when (windSpeedUnit) {
                            "meterPerSec" -> "$windSpeed"
                            "milesPerHour" -> "%.2f".format(convertToMilesPerHour(windSpeed))
                            else -> "$windSpeed" // Fallback to meters per second
                        }

                        val windUnitText = when (windSpeedUnit) {
                            "meterPerSec" -> "m/s"
                            "milesPerHour" -> "mph"
                            else -> "m/s" // Fallback to meters per second
                        }
                        binding.windUnit.text = windUnitText
                        binding.windSpeed.text = formattedWindSpeedText
                        binding.tvPressureValue.text = response.main.pressure.toString()
                        val imageUrl = iconsUrl + response.weather[0].icon + "@2x.png"
                        Glide.with(requireContext()).load(imageUrl)
                            .apply(
                                RequestOptions()
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_foreground)
                            )
                            .into(binding.ivIcon)
                    }
                    is ApiState.Failure -> {
                        Log.i("TAG", "onCreateView: Failure")

                    }

                }

            }
        }

        lifecycleScope.launch {
            forecastViewModel.apiState.collectLatest { state ->
                when(state) {
                    is ApiState.Loading -> {
                        Log.i("TAG", "onCreateView: Loading")
                        binding.progressBar.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    }
                    is ApiState.Success -> {
                        Log.i("TAG", "onCreateView: Success")
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE
                        var response = state.weather as Forecast
                        var currentDate = response.list[0].dt_txt.split(" ")[0]
                        binding.tvDate.text = currentDate
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("h a", Locale.getDefault())

                        val date = inputFormat.parse(response.list[0].dt_txt)

                        val formattedTime = outputFormat.format(date)
                        binding.tvTimee.text = formattedTime
                        hourlyAdapter.submitList(response.list.filter {
                            val date = it.dt_txt.split(" ")[0]
                            date == currentDate
                        })
                        val dailyWeatherList = createDailyWeatherList(response)
                        dailyAdapter.submitList(dailyWeatherList)
                    }
                    is ApiState.Failure -> {
                        Log.i("TAG", "onCreateView: Failure")
                    }

                }
            }
        }

        if (latitude != null && longitude != null) {
            Log.d("WeatherFragment", "Using provided latitude: $latitude, longitude: $longitude")
            weatherViewModel.getWeather(latitude, longitude, "metric", "en")
            forecastViewModel.getForecast(latitude, longitude, "metric", "en")
        } else {
            updateUI(language!!)
        }

        return binding.root
    }

    private fun convertToMilesPerHour(metersPerSecond: Double): Double {
        return metersPerSecond * 2.237 // 1 m/s is approximately 2.237 mph
    }

    fun updateUI(language: String) {
        fusedLocationproviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                super.onLocationResult(location)
                latitude = location.locations[0].latitude
                longitude = location.locations[0].longitude
                Log.i("Location", "onLocationResult: $latitude : $longitude")

                weatherViewModel.getWeather(latitude, longitude, "metric", language)
                forecastViewModel.getForecast(latitude, longitude, "metric", language)
                fusedLocationproviderClient.removeLocationUpdates(this)
            }
        }

        fusedLocationproviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            locationCallback,
            Looper.myLooper()
        )
    }


    private fun createDailyWeatherList(forecast: Forecast): List<DailyWeather> {
        val dailyWeatherList = mutableListOf<DailyWeather>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val seenDates = mutableSetOf<String>()

        forecast.list.forEach { forecastElement ->
            val date = dateFormat.format(forecastElement.dt * 1000L)
            if (date !in seenDates) {
                seenDates.add(date)
                dailyWeatherList.add(
                    DailyWeather(
                        date = date,
                        maxTemp = forecastElement.main.temp_max.toInt(),
                        minTemp = forecastElement.main.temp_min.toInt(),
                        icon = forecastElement.weather[0].icon
                    )
                )
            }
        }
        return dailyWeatherList
    }

}