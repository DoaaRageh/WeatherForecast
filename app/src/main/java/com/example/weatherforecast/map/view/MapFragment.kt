package com.example.weatherforecast.map.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.db.AppDataBase
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.ApiState
import com.example.weatherforecast.network.WeatherRemoteDataSource
import com.example.weatherforecast.home.view.HomeFragment
import com.example.weatherforecast.home.viewmodel.WeatherViewModel
import com.example.weatherforecast.home.viewmodel.WeatherViewModelFactory
import com.example.weatherforecast.model.AlarmRoom

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay

class MapFragment : Fragment(), MapEventsReceiver {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var marker: Marker? = null
    private var lastTouchedGeoPoint: GeoPoint? = null
    private lateinit var repository: WeatherRepository
    lateinit var binding: FragmentMapBinding
    private lateinit var forecast: Forcast
    private lateinit var weatherFactory: WeatherViewModelFactory
    private lateinit var weatherViewModel: WeatherViewModel
    var latitude = 0.0
    var longitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        binding = FragmentMapBinding.inflate(inflater, container, false )
        forecast = Forcast("", 0.0, 0.0)
        repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(AppDataBase.getInstance(requireContext()).getForecastDao()))
        weatherFactory = WeatherViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(AppDataBase.getInstance(requireContext()).getForecastDao()))
        )

        weatherViewModel = ViewModelProvider(this, weatherFactory).get(WeatherViewModel::class.java)



        lifecycleScope.launch {
            weatherViewModel.apiState.collectLatest { state ->
                when(state) {
                    is ApiState.Loading -> {
                        Log.i("TAG", "onCreateView: Loading")
                    }
                    is ApiState.Success -> {
                        val fragment = prefs.getString("fragment", "home")
                        Log.i("TAG", "onCreateView: Success")
                        var response = state.weather as WeatherResponse
                        forecast.city = response.name
                        forecast.lat = lastTouchedGeoPoint?.latitude!!
                        forecast.lon = lastTouchedGeoPoint?.longitude!!
                        forecast = Forcast(response.name, lastTouchedGeoPoint?.latitude!!, lastTouchedGeoPoint?.longitude!!)
                        if(fragment == "fav") {
                            prefs.edit().putString("fragment", "home").apply()
                            repository.insertForecast(forecast)
                        }


                    }
                    is ApiState.Failure -> {
                        Log.i("TAG", "onCreateView: Failure")
                    }

                }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnAdd.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                val homeFragment = HomeFragment()

                // Create a Bundle to hold the latitude and longitude
                val args = Bundle().apply {
                    putDouble("latitude", latitude)
                    putDouble("longitude", longitude)
                }

                // Set the arguments to the fragment
                homeFragment.arguments = args

                // Begin the transaction to replace the current fragment with WeatherFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, homeFragment)
                    .addToBackStack(null) // Optional: Add to back stack to allow navigating back
                    .commit()
            }
        }



        setupMap()
        return binding.root
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.setMultiTouchControls(true)

        binding.mapView.minZoomLevel = 3.0
        binding.mapView.maxZoomLevel = 18.0
        binding.mapView.controller.setZoom(15.0)


        val latitude = arguments?.getDouble("LATITUDE") ?: 30.0603656
        val longitude = arguments?.getDouble("LONGITUDE") ?: 31.384177
        val initialLocation = GeoPoint(latitude, longitude)
        // Set initial marker location

        /*val marker = Marker(binding.mapView)

        marker.position = initialLocation

        val drawable = resources.getDrawable(R.drawable.placeholder, null)
        marker.icon = drawable

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.overlays.add(marker)


        binding.mapView.controller.setCenter(initialLocation)*/

        marker = Marker(binding.mapView).apply {

            position = initialLocation
            icon = resources.getDrawable(R.drawable.placeholder, null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Initial Location"
        }
        binding.mapView.overlays.add(marker!!)
        binding.mapView.controller.setCenter(initialLocation)

        // Add MapEventsOverlay to detect touch events
        val mapEventsOverlay = MapEventsOverlay(this)
        binding.mapView.overlays.add(mapEventsOverlay)
    }

    // Called when user performs a single tap on the map
    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
        lastTouchedGeoPoint = p
        placeMarkerAtLastTouchedLocation()
        return true
    }

    // Called when user performs a long press on the map
    override fun longPressHelper(p: GeoPoint): Boolean {
        return false
    }

    private fun placeMarkerAtLastTouchedLocation() {
        lastTouchedGeoPoint?.let { geoPoint ->
            weatherViewModel.getWeather(geoPoint.latitude, geoPoint.longitude, "en")

            // Remove the old marker if it exists
            marker?.let {
                binding.mapView.overlays.remove(it)
            }

            // Create a new marker at the last touched location
            marker = Marker(binding.mapView).apply {
                position = geoPoint
                icon = resources.getDrawable(R.drawable.placeholder, null)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "New Marker"
            }
            binding.mapView.overlays.add(marker!!)
            binding.mapView.invalidate() // Refresh the map to show the new marker

            // Set latitude and longitude correctly
            latitude = geoPoint.latitude
            longitude = geoPoint.longitude

            // Optionally show a toast with the coordinates
            Toast.makeText(requireContext(), "Lat: $latitude, Lon: $longitude", Toast.LENGTH_SHORT).show()

        } ?: run {
            Toast.makeText(requireContext(), "Please touch the map first!", Toast.LENGTH_SHORT).show()
        }
    }
}
