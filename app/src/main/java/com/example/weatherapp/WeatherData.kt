package com.example.weatherapp

data class WeatherData (
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeather,
    val hourly: HourlyWeather,
)

data class CurrentWeather(
    val time: String,
    val temperature_2m : Double // bad?
)
data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Double>
)

data class WeatherByHour(
    val id: Number,
    var time: String,
    val temperature: Double
)

data class WeatherByDay(
    val id: Number,
    val date: String,
    val dayTemp : Number,
    val nightTemp: Number,
    val byHour: List<WeatherByHour>

)

data class WeatherStack(
    val byDay: List<WeatherByDay>
)