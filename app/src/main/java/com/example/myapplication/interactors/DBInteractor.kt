package com.example.myapplication.interactors
import com.example.myapplication.repo.Repository
import javax.inject.Inject

class DBInteractor @Inject constructor (
        var repository: Repository
) {
    fun getAll() : ArrayList<String> {
        return repository.getAll()
    }

    fun insert(string: String) {
        repository.insert(string)
    }
}