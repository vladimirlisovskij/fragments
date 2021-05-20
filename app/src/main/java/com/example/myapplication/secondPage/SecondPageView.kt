package com.example.myapplication.secondPage

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

interface SecondPageView : MvpView {
    @Skip
    fun setItems(strings: ArrayList<String>)
}