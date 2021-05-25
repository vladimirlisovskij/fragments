package com.example.myapplication.interactors

import android.location.Location
import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.WeatherContainer
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import javax.inject.Inject

class APIInteractor @Inject constructor (
    private val repository: Repository
) {
    fun getAll(){
        return repository.getWeather()
    }

    fun setToastCallback(callBack: (String) -> Unit){
        repository.toastCallback = callBack
    }

    fun setLocationCallback( callback: (() -> Unit) ) {
        repository.locationCallback = callback
    }

    fun getLocationCallback() : ((Location?) -> Unit) {
        return repository::setLocation
    }

    fun setCallback( callBack: ((WeatherContainer) -> Unit) ) {
        repository.weatherCallback = callBack
    }

    fun setCityId(id: Int) {
        repository.setCityId(id)
    }

    fun watch() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(repository, "repo")
        repository.watch()
    }
}