package com.example.myapplication.firstPage

//import com.example.myapplication.retrofit.DaggerRetrofitComponent
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

    @Inject
    lateinit var toster: Toster

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refresh()
    }

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toster.makeToast(throwable.localizedMessage ?: "BUG")
    }

    fun refresh() {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            val container = apiInteractor.getAll()
            withContext(Dispatchers.Main) {
                viewState.setInfo(container)
            }
        }
    }
}