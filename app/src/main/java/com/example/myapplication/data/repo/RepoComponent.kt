package com.example.myapplication.data.repo

import com.example.myapplication.domain.interactors.api.APIRepo
import com.example.myapplication.domain.interactors.database.DBRepo
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component
interface RepoComponent {
    @Singleton
    fun getRepo() : Repository
}

@Module
class RepoModule {
    private val repository = DaggerRepoComponent.create().getRepo()

    @Provides
    fun provideAPI() : APIRepo
    {
        return repository
    }

    @Provides
    fun provideDB() : DBRepo
    {
        return repository
    }
}