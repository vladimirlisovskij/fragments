package com.example.myapplication.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Employee (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val cityName: String
)