package com.example.myapplication.interactors

import com.example.myapplication.DaggerMainComponent
import dagger.Module

@Module
class InteractorsModule {
    fun getAPI() : APIInteractor {
        return APIInteractor(DaggerMainComponent.builder().build().getRepository())
    }

    fun getDB() : DBInteractor {
        return DBInteractor(DaggerMainComponent.builder().build().getRepository())
    }
}