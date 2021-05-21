package com.example.myapplication.interactors
import com.example.myapplication.repo.Repository
import com.example.myapplication.room.Employee
import javax.inject.Inject

class DBInteractor @Inject constructor (
        var repository: Repository
) {
    fun getApi() {
        return repository.getApi()
    }

    fun setGetCallback(callBack: ((ArrayList<Employee>) -> Unit) ) {
        repository.apiCallback = callBack
    }

    fun setInsertCallback(callBack: (Employee?) -> Unit) {
        repository.insertCallback = callBack
    }

    fun insert(string: String) {
        repository.insert(string)
    }
}