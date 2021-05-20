package com.example.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Employee (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val cityID: String,
    val cityName: String
)