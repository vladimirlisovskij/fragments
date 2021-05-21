package com.example.myapplication.room

import com.example.myapplication.contextHolder.ContextHolder
import javax.inject.Inject

class DAOBuilder @Inject constructor(
    private val contextHolder: ContextHolder
){
    fun getDAO() : EmployeeDAO {
        return AbstractDB.getDB(contextHolder.getContext()).employeeDAO()
    }
}
