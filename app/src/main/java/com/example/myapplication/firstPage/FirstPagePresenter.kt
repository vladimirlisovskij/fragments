package com.example.myapplication.firstPage

import com.example.myapplication.interactors.APIInteractor
import com.example.myapplication.toster.Toster
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class FirstFragmentPresenter : MvpPresenter<FirstPageView>() {

    @Inject
    lateinit var apiInteractor: APIInteractor

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        apiInteractor.setCallback {
            viewState.setInfo(it)
            viewState.stopRefresh()
        }
        refresh()
    }

    fun refresh() {
        viewState.startRefresh()
        apiInteractor.getAll()
    }
}