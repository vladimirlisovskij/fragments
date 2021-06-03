package com.example.myapplication.presenter.mainActivity

import com.github.terrakok.cicerone.Router
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val router: Router
) : MvpPresenter<MainView>() {
    private var isSecondPage = false

    fun openFirst() {
        router.newRootScreen(Screens.screen2)
        router.navigateTo(Screens.screen1)
        isSecondPage = true
        viewState.moveToFirst()
    }

    fun toFirst() {
        if (isSecondPage) {
            router.replaceScreen(Screens.screen1)
        } else {
            router.navigateTo(Screens.screen1)
            isSecondPage = true
        }
        viewState.moveToFirst()
    }

    fun toSecond() {
        router.newRootScreen(Screens.screen2)
        isSecondPage = false
        viewState.moveToSecond()
    }

    fun toThird() {
        if (isSecondPage) {
            router.replaceScreen(Screens.screen3)
        } else {
            router.navigateTo(Screens.screen3)
            isSecondPage = true
        }
        viewState.moveToThird()
    }

    fun onBack() {
        router.exit()
        isSecondPage = false
        viewState.moveToSecond()
    }
}