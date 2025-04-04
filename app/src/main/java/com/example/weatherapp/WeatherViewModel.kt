package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class WeatherViewModel(): ViewModel() {
    private val weatherRepository = WeatherRepository()

    val client = HttpClient(CIO)
    private val _isLoading = MutableStateFlow(false)
    private val _weather = MutableStateFlow(WeatherStack(emptyList()))
    val weatherData: MutableStateFlow<WeatherStack> = _weather
    val isLoading: StateFlow<Boolean> = _isLoading

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun getWeather(lat: Double, lon: Double) {
        _isLoading.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            weatherRepository.getWeekWeather(lat, lon) { data ->
                if(data is WeatherData){
                    val weatherStack :WeatherStack = mapToWeatherByDay(data)
                    _weather.value = weatherStack
                }
                _isLoading.value = false
            }
        },2000)
    }
    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    fun mapToWeatherByDay(response: WeatherData): WeatherStack {
        val weatherList = mapToWeatherByHour(response)
        var weatherByHour = mutableListOf<WeatherByHour>()
        var weatherByDay = mutableListOf<WeatherByDay>()
        var tempDay: Double = 0.0
        var tempNight: Double = 0.0
        weatherList.forEachIndexed { index, value ->
            var ItemId = index.toInt() + 1
            if(ItemId % 24 == 0){
                tempNight = tempNight + value.temperature
                val dateTime = LocalDateTime.parse(value.time)
                val month = dateTime.month
                val dayOfWeek = dateTime.dayOfWeek
                val formattedDate = "${month.name.lowercase().replaceFirstChar { it.uppercase() }} - ${dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}"

                weatherByDay.add(WeatherByDay(id = index, date = formattedDate, dayTemp = Math.round(tempDay/11).toInt(), nightTemp = Math.round(tempNight/13).toInt(), byHour = weatherByHour.toList()))
                tempDay = 0.0
                tempNight = 0.0
                weatherByHour.clear()
            }else{
                if (8 < ItemId % 24 && ItemId % 24 < 20 ){
                    tempDay = tempDay + value.temperature
                }else{
                    tempNight = tempNight + value.temperature
                }
                if (ItemId % 2 == 0){
                    val dateTime = LocalDateTime.parse(value.time)
                    value.time = "${dateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
                    } - ${String.format("%02d", dateTime.hour)}:00"
                    weatherByHour.add(value)
                }
            }

        }
        return WeatherStack(byDay = weatherByDay)
    }

    fun mapToWeatherByHour(response: WeatherData): List<WeatherByHour> {
        return response.hourly.time.zip(response.hourly.temperature_2m)
            .mapIndexed { index, (time, temp) ->
            WeatherByHour(id = index, time = time, temperature = temp)
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}