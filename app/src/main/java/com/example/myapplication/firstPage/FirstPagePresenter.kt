package com.example.myapplication.firstPage

import com.example.myapplication.injectApplication.InjectApplication
import com.example.myapplication.interactors.APIInteractor
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
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

    override fun onDestroy() {
        super.onDestroy()
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(apiInteractor, "API INTERACTOR")
        apiInteractor.watch()
    }

    fun refresh() {
        viewState.startRefresh()
        apiInteractor.getAll()
    }
}