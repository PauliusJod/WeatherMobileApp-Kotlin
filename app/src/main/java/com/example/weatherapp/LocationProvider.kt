package com.example.weatherapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.net.toUri


class LocationProvider(private val context: Context) {
    val latitude = MutableStateFlow(0.0)
    val longitude = MutableStateFlow(0.0)
    val isLocationLoaded = MutableStateFlow(false)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun requestLocationPermission(requestPermissionLauncher: ActivityResultLauncher<String>){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            } else {
                Toast.makeText(context, "Location permission required. Enable it in settings.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = "package:${context.packageName}".toUri()
                context.startActivity(intent)
            }
        }else{
            getLocation()
        }
    }

    fun getLocation() {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ){
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if(location != null){
                        latitude.value = location.latitude
                        longitude.value = location.longitude
                        Toast.makeText(context, "Loading weather data", Toast.LENGTH_LONG)
                            .show()
                        isLocationLoaded.value = true
                    }else{
                        Toast.makeText(context, "Please enable location services on your device", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }
}