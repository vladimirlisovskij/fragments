package com.example.myapplication.presenter.firstPage

import com.example.myapplication.data.retrofit.WeatherContainer
import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState
import moxy.viewstate.strategy.alias.Skip

interface FirstPageView : MvpView {
    @SingleState
    fun setInfo(container: WeatherContainer)

    @Skip
    fun startRefresh()

    @Skip
    fun stopRefresh()
}