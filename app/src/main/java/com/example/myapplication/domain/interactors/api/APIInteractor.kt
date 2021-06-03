package com.example.myapplication.domain.interactors.api

import android.location.Location
import com.example.myapplication.data.ktor.WeatherContainer
import javax.inject.Inject

class APIInteractor @Inject constructor (
    private val repository: APIRepo
) {
    fun getAll(){
        return repository.getWeather()
    }

    fun setToastCallback(callBack: (String) -> Unit){
        repository.setToastCallback(callBack)
    }

    fun setLocationCallback( callback: (() -> Unit) ) {
        repository.setLocationCallback(callback)
    }

    fun getLocationCallback() : ((Location?) -> Unit) {
        return repository.getLocationCallback()
    }

    fun setCallback( callBack: ((WeatherContainer) -> Unit) ) {
        repository.setWeatherCallback(callBack)
    }

    fun setCityId(id: Int) {
        repository.setCityId(id)
    }
}