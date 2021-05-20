package com.example.myapplication.interactors

import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.WeatherContainer
import javax.inject.Inject

class APIInteractor @Inject constructor (
        var repository: Repository
) {
    fun getAll(){
        return repository.getWeather()
    }

    fun setCallback( callBack: ((WeatherContainer) -> Unit) ) {
        repository.weatherCallback = callBack
    }

    fun setCityId(id: Int) {
        repository.setCityId(id)
    }
}