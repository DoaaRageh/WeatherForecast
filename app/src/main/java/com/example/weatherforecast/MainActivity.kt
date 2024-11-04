package com.example.weatherforecast

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.weatherforecast.alert.view.AlertFragment
import com.example.weatherforecast.favorite.view.FavoriteFragment
import com.example.weatherforecast.map.view.MapFragment
import com.example.weatherforecast.setting.view.SettingFragment
import com.example.weatherforecast.weather.view.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val REQUEST_LOCATION_CODE = 3
    lateinit var trns: FragmentTransaction
    lateinit var location: String
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        location = prefs.getString("location", "gps").toString()

        val mgr: FragmentManager = supportFragmentManager
        trns = mgr.beginTransaction()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnNavigationItemSelectedListener { item ->
            loadSelectedBottomNavigationFragment(item.itemId)
            true
        }
    }

    override fun onStart() {
        super.onStart()
        if(checkPermessions()) {
            if (isLocationEnabled()) {
                //loadHomeFragment()
                location = prefs.getString("location", "gps").toString()
                if (location == "gps") {
                    // Use GPS to get location
                    // Implement your logic to fetch weather based on GPS location
                    showFragment(HomeFragment())
                } else {
                    // Open map and get location
                    showFragment(MapFragment())
                }
            }
            else {
                enableLocationServices()
            }
        }
        else {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_CODE
            )
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION_CODE) {
            if(grantResults.size > 1 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                loadSelectedBottomNavigationFragment(R.id.item1)
            }
        }
    }

    fun checkPermessions(): Boolean {
        var result = false
        if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            result = true
        }
        return result
    }

    fun enableLocationServices() {
        Toast.makeText(this, "Turn on Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun loadHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun loadSelectedBottomNavigationFragment(itemId: Int) {
        var selectedFragment: Fragment? = null
        if (itemId == R.id.item1) {
            location = prefs.getString("location", "gps").toString()
            if (location == "gps") {
                selectedFragment = HomeFragment()
            } else {
                selectedFragment = MapFragment()
            }

        } else if (itemId == R.id.item2) {
            selectedFragment = AlertFragment()

        } else if (itemId == R.id.item3) {
            selectedFragment = FavoriteFragment()
        } else if (itemId == R.id.item4) {
            selectedFragment = SettingFragment()
        }
        showFragment(selectedFragment)
    }

     fun showFragment(fragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}