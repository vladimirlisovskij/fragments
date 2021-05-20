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

    @Inject
    lateinit var toster: Toster

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toster.makeToast(throwable.message ?: "BUG")
    }

    fun getItems() {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            val res = dbInteractor.getAll()
            withContext(Dispatchers.Main) {
                viewState.setItems(res)
            }
        }
    }

    fun setCity(id: Int) {
        apiInteractor.setCityId(id)
    }

    fun addItem(string: String) {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            dbInteractor.insert(string)
        }
    }
}