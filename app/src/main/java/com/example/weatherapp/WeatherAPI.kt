package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("forecast")
    fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m",
        @Query("hourly") hourly: String = "temperature_2m,apparent_temperature"
    ): Call<WeatherData>
}
