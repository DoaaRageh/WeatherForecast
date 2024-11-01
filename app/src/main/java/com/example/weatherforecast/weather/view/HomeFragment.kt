package com.example.weatherforecast.weather.view

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
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.model.Forecast
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.ApiState
import com.example.weatherforecast.network.WeatherRemoteDataSource
import com.example.weatherforecast.weather.viewmodel.ForecastViewModel
import com.example.weatherforecast.weather.viewmodel.ForecastViewModelFactory
import com.example.weatherforecast.weather.viewmodel.WeatherViewModel
import com.example.weatherforecast.weather.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var weatherFactory: WeatherViewModelFactory
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var forecastFactory: ForecastViewModelFactory
    private lateinit var forecastViewModel: ForecastViewModel
    lateinit var binding: FragmentHomeBinding
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var fusedLocationproviderClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    val iconsUrl = "https://openweathermap.org/img/wn/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false )

        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        var language = prefs.getString("language", "en")

        hourlyAdapter = HourlyAdapter(requireContext()){}

        binding.hourlyRecyclerView.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }

        weatherFactory = WeatherViewModelFactory(
            WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(requireContext()))
        )

        weatherViewModel = ViewModelProvider(this, weatherFactory).get(WeatherViewModel::class.java)

        forecastFactory = ForecastViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(requireContext()))
        )

        forecastViewModel = ViewModelProvider(this, forecastFactory).get(ForecastViewModel::class.java)


        lifecycleScope.launch {
            weatherViewModel.apiState.collectLatest { state ->
                when(state) {
                    is ApiState.Loading -> {
                        Log.i("TAG", "onCreateView: Loading")
                    }
                    is ApiState.Success -> {
                        Log.i("TAG", "onCreateView: Success")
                        var response = state.weather as WeatherResponse
                        binding.tvTempreture.text = response.main.temp.toString()
                        binding.tvDescription.text = response.weather[0].description
                        binding.tvCity.text = response.name
                        binding.windSpeed.text = response.wind.speed.toString()
                        binding.tvHumidity.text = response.main.humidity.toString() + "%"
                        binding.tvCloud.text = response.clouds.all.toString() + "%"
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
                    }
                    is ApiState.Success -> {
                        Log.i("TAG", "onCreateView: Success")
                        var response = state.weather as Forecast
                        var currentDate = response.list[0].dt_txt.split(" ")[0]
                        hourlyAdapter.submitList(response.list.filter {
                            val date = it.dt_txt.split(" ")[0]
                            date == currentDate
                        })
                    }
                    is ApiState.Failure -> {
                        Log.i("TAG", "onCreateView: Failure")
                    }

                }
            }
        }

        updateUI(language!!)

        return binding.root
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


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}