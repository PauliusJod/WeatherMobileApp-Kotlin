package com.example.weatherapp

import android.os.Bundle
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: LocationProvider

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = LocationProvider(this)
        locationProvider.requestLocationPermission(requestPermissionLauncher)

        enableEdgeToEdge()
        setContent {
            val weatherModel = viewModel<WeatherViewModel>()
            val latitude by locationProvider.latitude.collectAsStateWithLifecycle()
            val longitude by locationProvider.longitude.collectAsStateWithLifecycle()
            WeatherAppTheme {
                Surface(modifier = Modifier
                    .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {

                Scaffold (modifier = Modifier.fillMaxSize()){ innerPadding ->
                    val isLoading by weatherModel.isLoading.collectAsStateWithLifecycle()
                    val weatherData by weatherModel.weatherData.collectAsStateWithLifecycle()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ){
                        if(latitude == 0.0){
                            Column(modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                            Text(text = "Please enable location on your phone")
                                CircularProgressIndicator( modifier = Modifier.padding(20.dp))
                            Button(modifier = Modifier,
                                onClick = {
                                    finishAffinity()
                                    exitProcess(0)
                                }) { Text(text="Exit app")}
                            }
                        }else{
                            LaunchedEffect(latitude, longitude) {
                                if (latitude != 0.0 && longitude != 0.0) {
                                    weatherModel.getWeather(latitude, longitude)
                                }
                            }
                            if(isLoading){
                                CircularProgressIndicator()
                            }else{
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "Next 7 days weather",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    DisplayWeather(weatherData)
                                }

                            }
                        }
                    }
                }
            }
            }
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
            isGranted ->
            if (isGranted) {
                locationProvider.getLocation()

            } else {
                Toast.makeText(this, "Permission denied. Cannot get location.544", Toast.LENGTH_LONG).show()
                finishAffinity()
                exitProcess(0)
            }
        }
}

@Composable
fun DisplayWeather(data: WeatherStack) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(data.byDay) { byDay ->
            var isExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isExpanded = !isExpanded
                        }
                        .padding(16.dp)
                ) {
                    Text(
                        text = byDay.date,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherIconText(
                            icon = Icons.Filled.WbSunny,
                            text = "${byDay.dayTemp}°C",
                            iconColor = Color.Yellow
                        )
                        WeatherIconText(
                            icon = Icons.Filled.NightsStay,
                            text = "${byDay.nightTemp}°C",
                            iconColor = Color.Blue
                        )
                    }
                    if (isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            byDay.byHour.forEach { item ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        color = Color.Black,
                                        text = item.time,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            color = Color.Black,
                                            text = "${item.temperature}°C",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherIconText(icon: ImageVector, text: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}