package com.example.myapplication.data.ktor

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import javax.inject.Inject

class KtorBuilder @Inject constructor() {
    private companion object Urls {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
        const val KEY = "30796e96fd16433e49bdf7b5fd4ac746"
    }

    private val ktor = HttpClient(Android) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }

        engine {
            connectTimeout = 30_000
            socketTimeout = 10_000
        }
    }

    suspend fun getIDRequest(id: Int) : JSONData {
        return ktor.get(BASE_URL) {
            parameter("id", id)
            parameter("units", "metric")
            parameter("appid", KEY)
        }
    }

    suspend fun getGeoRequest(lat: Double, lon: Double) : JSONData {
        return ktor.get(BASE_URL) {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("appid", KEY)
        }
    }
}