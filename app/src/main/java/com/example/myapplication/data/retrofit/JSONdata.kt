package com.example.myapplication.data.retrofit

class Weather {
    val main: String? = null
    val description: String? = null
}

class Wind {
    val speed: Double? = null
}

class Main {
    val temp: Double? = null
    val humidity: Long? = null
}

class Sys {
    val sunrise: Long? = null
    val sunset: Long? = null
}

class JSONData {
    val weather: List<Weather>? = null
    val wind: Wind? = null
    val sys: Sys? = null
    val main: Main? = null
    val name: String? = null
    val id: String? = null
}