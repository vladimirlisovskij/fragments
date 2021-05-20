package com.example.myapplication

import com.example.myapplication.activityHolder.ActivityHolder
import com.example.myapplication.activityHolder.ActivityModule
import com.example.myapplication.contextHolder.ContextHolder
import com.example.myapplication.contextHolder.ContextModule
import com.example.myapplication.firstPage.FirstFragmentPresenter
import com.example.myapplication.interactors.APIInteractor
import com.example.myapplication.interactors.DBInteractor
import com.example.myapplication.interactors.InteractorsModule
import com.example.myapplication.repo.Repository
import com.example.myapplication.retrofit.RetrofitBuilder
import com.example.myapplication.room.DAOBuilder
import com.example.myapplication.secondPage.SecondPagePresenter
import com.example.myapplication.toster.Toster
import dagger.Component

@Component(modules = [ContextModule::class, InteractorsModule::class, ActivityModule::class])
interface MainComponent {
    fun getContext() : ContextHolder
    fun getApi() : APIInteractor
    fun getDB() : DBInteractor
    fun getRepository() : Repository
    fun getRetrofit() : RetrofitBuilder
    fun getDAOModule() : DAOBuilder
    fun getToster() : Toster
    fun getActivity() : ActivityHolder

    fun inject(firstFragmentPresenter: FirstFragmentPresenter)
    fun inject(secondPagePresenter: SecondPagePresenter)
}
