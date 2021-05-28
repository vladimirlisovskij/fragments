package com.example.myapplication.data.room

import com.example.myapplication.presenter.injectApplication.MainApplication
import javax.inject.Inject

class DAOBuilder @Inject constructor(){
    fun getDAO() : EmployeeDAO {
        return AbstractDB
            .getDB(MainApplication.getInstance())
            .employeeDAO()
    }
}
