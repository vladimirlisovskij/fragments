package com.example.myapplication.interactors
import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.WeatherContainer
import javax.inject.Inject

class DBInteractor @Inject constructor (
        var repository: Repository
) {
    fun getApi() {
        return repository.getApi()
    }

    fun setCallback( callBack: ((ArrayList<String>) -> Unit) ) {
        repository.apiCallback = callBack
    }

    fun insert(string: String) {
        repository.insert(string)
    }
}