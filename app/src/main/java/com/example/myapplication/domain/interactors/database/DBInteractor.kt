package com.example.myapplication.domain.interactors.database

import com.example.myapplication.data.sqlite.SQLiteModule
import javax.inject.Inject

class DBInteractor @Inject constructor (
    private val repository: DBRepo
) {
    fun getApi() {
        return repository.getApi()
    }

    fun setToastCallback(callBack: (String) -> Unit){
        repository.setToastCallback(callBack)
    }

    fun setGetCallback(callBack: ((ArrayList<SQLiteModule.City>) -> Unit) ) {
        repository.setGetCallback(callBack)
    }

    fun setInsertCallback(callBack: (SQLiteModule.City?) -> Unit) {
        repository.setInsertCallback(callBack)
    }

    fun insert(string: String) {
        repository.insert(string)
    }
}