package com.example.myapplication.data.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ServerApi {
    companion object Urls {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun getRequest(id: Int) : String {
            return "weather" + // текущая погода
                    "?id=" + id.toString() +
                    "&units=metric" + // градусы Цельсия
                    "&appid=30796e96fd16433e49bdf7b5fd4ac746"
        }

        fun getGeoRequest(lat: Double, lon: Double) : String {
            return "weather" + // текущая погода
                    "?lat=" + lat.toString() +
                    "&lon=" + lon.toString() +
                    "&units=metric" + // градусы Цельсия
                    "&appid=30796e96fd16433e49bdf7b5fd4ac746"
        }
    }

    @GET
    suspend fun getMessage(@Url url: String): Response<JSONData>
}