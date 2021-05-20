package com.example.myapplication.interactors

import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.WeatherContainer
import javax.inject.Inject

class APIInteractor @Inject constructor (
        var repository: Repository
) {
    suspend fun getAll() : WeatherContainer {
        return repository.getServerResponse()
    }

    fun setCityId(id: Int) {
        repository.setCityId(id)
    }
}