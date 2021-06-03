package com.example.myapplication.presenter.firstPage

import com.example.myapplication.domain.interactors.api.APIInteractor
import com.example.myapplication.presenter.injectApplication.MainApplication
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

    fun refresh() {
        viewState.startRefresh()
        apiInteractor.getAll()
    }
}