package com.example.myapplication.secondPage

import com.example.myapplication.interactors.APIInteractor
import com.example.myapplication.interactors.DBInteractor
import com.example.myapplication.toster.Toster
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class SecondPagePresenter : MvpPresenter<SecondPageView>() {
    @Inject
    lateinit var dbInteractor: DBInteractor

    @Inject
    lateinit var apiInteractor: APIInteractor

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        dbInteractor.setCallback {
            viewState.setItems(it)
        }
    }

    fun getItems() {
        dbInteractor.getApi()
    }

    fun setCity(id: Int) {
        apiInteractor.setCityId(id)
    }

    fun addItem(string: String) {
        dbInteractor.insert(string)
    }
}