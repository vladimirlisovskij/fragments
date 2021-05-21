package com.example.myapplication.injectApplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.myapplication.activityHolder.ActivityModule
import com.example.myapplication.contextHolder.ContextModule
import com.example.myapplication.mainComponent.DaggerMainComponent
import com.example.myapplication.mainComponent.MainComponent

class InjectApplication : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        private lateinit var injectApplication: InjectApplication

        fun getInjector() : MainComponent = injectApplication.getMainComponent()
        fun getInstance() : InjectApplication = injectApplication
    }

    override fun onCreate() {
        super.onCreate()
        injectApplication = this
    }

    private lateinit var mainComponent: MainComponent
    fun getMainComponent() : MainComponent = mainComponent

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mainComponent = DaggerMainComponent
            .builder()
            .contextModule(ContextModule(applicationContext))
            .activityModule(ActivityModule(activity))
            .build()
    }


    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}