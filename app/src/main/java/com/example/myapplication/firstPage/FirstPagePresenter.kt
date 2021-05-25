package com.example.myapplication.firstPage

import com.example.myapplication.injectApplication.MainApplication
import com.example.myapplication.interactors.APIInteractor
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class FirstFragmentPresenter @Inject constructor(
    private val apiInteractor: APIInteractor
): MvpPresenter<FirstPageView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        apiInteractor.setCallback {
            viewState.setInfo(it)
            viewState.stopRefresh()
        }
        apiInteractor.setLocationCallback {
            MainApplication.getInstance().getLastLocation(apiInteractor.getLocationCallback())
        }
        apiInteractor.setToastCallback { str ->
            MainApplication.getInstance().makeToast(str)
        }
        refresh()
    }

    override fun onDestroy() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(apiInteractor, "API INTERACTOR")
        apiInteractor.watch()
        super.onDestroy()
    }

    fun refresh() {
        viewState.startRefresh()
        apiInteractor.getAll()
    }
}