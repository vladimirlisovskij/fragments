package com.example.myapplication.mainActivity

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    fun iSReady() {
        viewState.showFirstPage()
    }

    fun toFirst() {
        viewState.showFirstPage()
    }

    fun toSecond() {
        viewState.showSecondPage()
    }


    fun toThird() {
        viewState.showThirdPage()
    }
}