package com.example.weatherapp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository {

    fun getWeekWeather(lat: Double, lon: Double, callback: (WeatherData?) -> Unit) {

        RetrofitClient.api.getWeather(lat,lon).enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    callback(response.body() as WeatherData)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                callback(null)
            }
        })
    }
}