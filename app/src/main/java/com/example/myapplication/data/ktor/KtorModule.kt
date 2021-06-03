package com.example.myapplication.data.ktor

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import javax.inject.Inject

class KtorBuilder @Inject constructor() {
    private companion object Urls {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5"

        fun getIDRef(id: Int): String {
            return "weather" + // текущая погода
                    "?id=" + id.toString() +
                    "&units=metric" + // градусы Цельсия
                    "&appid=30796e96fd16433e49bdf7b5fd4ac746"
        }

        fun getGeoRef(lat: Double, lon: Double): String {
            return "weather" + // текущая погода
                    "?lat=" + lat.toString() +
                    "&lon=" + lon.toString() +
                    "&units=metric" + // градусы Цельсия
                    "&appid=30796e96fd16433e49bdf7b5fd4ac746"
        }
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
        return ktor.request("${BASE_URL}/${getIDRef(id)}")
    }

    suspend fun getGeoRequst(lat: Double, lon: Double) : JSONData {
        return ktor.request("${BASE_URL}/${getGeoRef(lat, lon)}")
    }
}