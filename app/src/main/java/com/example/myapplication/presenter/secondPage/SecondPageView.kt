package com.example.myapplication.presenter.secondPage

import com.example.myapplication.data.room.Employee
import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

interface SecondPageView : MvpView {
    @Skip
    fun setItems(strings: ArrayList<Employee>)

    @Skip
    fun addItem(string: Employee)

    @Skip
    fun showBar(state: Boolean)
}