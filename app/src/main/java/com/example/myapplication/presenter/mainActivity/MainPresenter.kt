package com.example.myapplication.presenter.mainActivity

import com.github.terrakok.cicerone.Router
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val router: Router
) : MvpPresenter<MainView>() {
    fun openFirst() {
        router.newRootScreen(
            Screens.screen2
        )
        router.navigateTo(
            Screens.screen1
        )
        viewState.moveToFirst()
    }

    fun toFirst() {
        router.newRootScreen(
            Screens.screen1
        )
        viewState.moveToFirst()
    }

    fun toSecond() {
        router.newRootScreen(
            Screens.screen2
        )
        viewState.moveToSecond()
    }

    fun toThird() {
        router.newRootScreen(
            Screens.screen3
        )
        viewState.moveToThird()
    }

    fun onBack() {
        router.exit()
        viewState.moveToSecond()
    }
}