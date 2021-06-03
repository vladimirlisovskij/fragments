package com.example.myapplication.domain.interactors.api

import android.location.Location
import com.example.myapplication.data.ktor.WeatherContainer

interface APIRepo {
    fun getWeather()

    fun setCityId(id: Int)

    fun setToastCallback(callBack: (String) -> Unit)

    fun setLocationCallback( callback: (() -> Unit) )

    fun getLocationCallback() : ((Location?) -> Unit)

    fun setWeatherCallback(callBack: ((WeatherContainer) -> Unit) )
}