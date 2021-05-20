package com.example.myapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmployeeDAO {

    @Query("select * from employee")
    fun getAll(): List<Employee>

    @Query("delete from employee where id=:id")
    fun deleteById(id: Int): Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(employee: Employee)
}