package com.example.myapplication.presenter.mainActivity

import com.example.myapplication.presenter.firstPage.FirstPage
import com.example.myapplication.presenter.secondPage.SecondPage
import com.example.myapplication.presenter.thirdPage.ThirdPageFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {
    val screen1 = FragmentScreen(){
        FirstPage.newInstance()
    }

    val screen2 =  FragmentScreen {
        SecondPage.newInstance()
    }

    val screen3 = FragmentScreen {
        ThirdPageFragment.newInstance()
    }
}