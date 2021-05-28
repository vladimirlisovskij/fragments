package com.example.myapplication.domain.interactors.database
import com.example.myapplication.data.room.Employee
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

    fun setGetCallback(callBack: ((ArrayList<Employee>) -> Unit) ) {
        repository.setGetCallback(callBack)
    }

    fun setInsertCallback(callBack: (Employee?) -> Unit) {
        repository.setInsertCallback(callBack)
    }

    fun insert(string: String) {
        repository.insert(string)
    }
}