package com.example.myapplication.secondPage

import com.example.myapplication.injectApplication.MainApplication
import com.example.myapplication.interactors.APIInteractor
import com.example.myapplication.interactors.DBInteractor
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
        apiInteractor.watch()
        dbInteractor.watch()
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