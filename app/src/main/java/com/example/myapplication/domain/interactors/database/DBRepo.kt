package com.example.myapplication.domain.interactors.database

import com.example.myapplication.data.sqlite.SQLiteModule

interface DBRepo {
    fun getApi()

    fun insert(string: String)

    fun setToastCallback(callBack: (String) -> Unit)

    fun setGetCallback(callBack: ((ArrayList<SQLiteModule.City>) -> Unit) )

    fun setInsertCallback(callBack: (SQLiteModule.City?) -> Unit)
}