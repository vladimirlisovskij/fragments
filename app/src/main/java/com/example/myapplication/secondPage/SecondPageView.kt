package com.example.myapplication.secondPage

import com.example.myapplication.room.Employee
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