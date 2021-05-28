package com.example.myapplication.presenter.mainActivity

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

interface MainView : MvpView {
    @Skip
    fun moveToFirst()

    @Skip
    fun moveToSecond()

    @Skip
    fun moveToThird()
}