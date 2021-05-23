package com.example.myapplication.room

import com.example.myapplication.injectApplication.MainApplication
import javax.inject.Inject

class DAOBuilder @Inject constructor(){
    fun getDAO() : EmployeeDAO {
        return AbstractDB
            .getDB(MainApplication.getInstance())
            .employeeDAO()
    }
}
