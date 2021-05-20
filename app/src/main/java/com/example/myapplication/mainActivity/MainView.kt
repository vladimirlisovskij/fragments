package com.example.myapplication.mainActivity

import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState

interface MainView : MvpView {
    @SingleState // SingleStateStrategy - добавить и отчистить очередь
    fun showFirstPage()

    @SingleState
    fun showSecondPage()

    @SingleState
    fun showThirdPage()
}