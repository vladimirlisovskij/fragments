package com.example.myapplication.retrofit

class Weather {
    val main: String? = null
    val description: String? = null
}

class Wind {
    val speed: Double? = null
}

class Main {
    var temp: Double? = null
    var humidity: Long? = null
}

class Sys {
    val sunrise: Long? = null
    val sunset: Long? = null
}

class JSONData {
    val weather: List<Weather>? = null
    val wind: Wind? = null
    var sys: Sys? = null
    val main: Main? = null
    val name: String? = null
}