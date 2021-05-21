package com.example.myapplication.interactors
import com.example.myapplication.repo.Repository
import com.example.myapplication.room.Employee
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import javax.inject.Inject

class DBInteractor @Inject constructor (
        private val repository: Repository
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

    fun watch() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(repository, "repo")
    }
}