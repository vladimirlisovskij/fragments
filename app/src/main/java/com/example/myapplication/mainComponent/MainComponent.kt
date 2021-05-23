package com.example.myapplication.mainComponent

import com.example.myapplication.firstPage.FirstFragmentPresenter
import com.example.myapplication.firstPage.FirstPage
import com.example.myapplication.interactors.APIInteractor
import com.example.myapplication.interactors.DBInteractor
import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.RetrofitBuilder
import com.example.myapplication.room.DAOBuilder
import com.example.myapplication.secondPage.SecondPagePresenter
import com.example.myapplication.toster.Toster
import dagger.Component

@Component
interface MainComponent {
    fun getFirstPresenter() : FirstFragmentPresenter
    fun getSecondPresenter() : SecondPagePresenter
}
