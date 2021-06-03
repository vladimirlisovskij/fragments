package com.example.myapplication.presenter.secondPage

import com.example.myapplication.data.sqlite.SQLiteModule
import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

interface SecondPageView : MvpView {
    @Skip
    fun setItems(strings: ArrayList<SQLiteModule.City>)

    @Skip
    fun addItem(string: SQLiteModule.City)

    @Skip
    fun showBar(state: Boolean)
}