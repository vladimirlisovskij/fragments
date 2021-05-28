package com.example.myapplication.domain.interactors.database

import com.example.myapplication.data.room.Employee

interface DBRepo {
    fun getApi()

    fun insert(string: String)

    fun setToastCallback(callBack: (String) -> Unit)

    fun setGetCallback(callBack: ((ArrayList<Employee>) -> Unit) )

    fun setInsertCallback(callBack: (Employee?) -> Unit)
}