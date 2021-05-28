package com.example.myapplication.presenter.mainComponent

import com.example.myapplication.data.repo.RepoModule
import com.example.myapplication.presenter.cicerone.NavigationModule
import com.example.myapplication.presenter.firstPage.FirstPage
import com.example.myapplication.presenter.mainActivity.MainActivity
import com.example.myapplication.presenter.secondPage.SecondPage
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepoModule::class, NavigationModule::class])
interface MainComponent {
    fun inject(firstPage: FirstPage)
    fun inject(secondPage: SecondPage)
    fun inject(target: MainActivity)
}
