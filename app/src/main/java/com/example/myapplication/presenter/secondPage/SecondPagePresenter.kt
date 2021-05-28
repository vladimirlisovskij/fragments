package com.example.myapplication.presenter.secondPage

import com.example.myapplication.domain.interactors.api.APIInteractor
import com.example.myapplication.domain.interactors.database.DBInteractor
import com.example.myapplication.presenter.injectApplication.MainApplication
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class SecondPagePresenter@Inject constructor(
    private val apiInteractor: APIInteractor,
    private val dbInteractor: DBInteractor
) : MvpPresenter<SecondPageView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        dbInteractor.setGetCallback {
            viewState.setItems(it)
            viewState.showBar(false)
        }
        dbInteractor.setInsertCallback {
            it?.let { viewState.addItem(it) }
            viewState.showBar(false)
        }
        dbInteractor.setToastCallback { str ->
            MainApplication.getInstance().makeToast(str)
        }
        apiInteractor.setToastCallback { str ->
            MainApplication.getInstance().makeToast(str)
        }
    }

    override fun onDestroy() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(apiInteractor, "API INTERACTOR")
        appWatcher.expectWeaklyReachable(dbInteractor, "API INTERACTOR")
        super.onDestroy()
    }

    fun getItems() {
        viewState.showBar(true)
        dbInteractor.getApi()
    }

    fun setCity(id: Int) {
        apiInteractor.setCityId(id)
    }

    fun addItem(string: String) {
        viewState.showBar(true)
        dbInteractor.insert(string)
    }
}