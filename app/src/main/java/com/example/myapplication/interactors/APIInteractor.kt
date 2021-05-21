package com.example.myapplication.interactors

import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.WeatherContainer
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
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

    fun watch() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(repository, "repo")
        repository.watch()
    }
}