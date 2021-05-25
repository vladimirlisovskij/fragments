package com.example.myapplication.mainComponent

import com.example.myapplication.firstPage.FirstPage
import com.example.myapplication.secondPage.SecondPage
import dagger.Component

@Component
interface MainComponent {
    fun inject(firstPage: FirstPage)
    fun inject(secondPage: SecondPage)
}
