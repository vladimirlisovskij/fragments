package com.example.myapplication.activityHolder

import android.app.Activity
import dagger.Module
import dagger.Provides

class ActivityHolder(
    private val activity: Activity
){
    fun getActivity() : Activity = activity
}

@Module
class ActivityModule(
    private val activity: Activity
) {
    @Provides
    fun getHolder() : ActivityHolder = ActivityHolder(activity)
}