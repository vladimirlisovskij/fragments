package com.example.myapplication.firstPage

import com.example.myapplication.retrofit.WeatherContainer
import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState

interface FirstPageView : MvpView {
    @SingleState // SingleStateStrategy - добавить и отчистить очередь
    fun setInfo(container: WeatherContainer)
}