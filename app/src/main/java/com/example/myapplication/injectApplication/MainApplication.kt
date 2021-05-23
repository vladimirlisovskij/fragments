package com.example.myapplication.injectApplication

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.myapplication.mainActivity.MainActivity

class MainApplication : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        private lateinit var instance: MainApplication
        fun getInstance() : MainApplication = instance
    }

    private var curActivity: MainActivity? = null

    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        curActivity = activity as MainActivity
    }

    override fun onActivityDestroyed(activity: Activity) {
        curActivity = null
    }

    fun getLastLocation(callBack: ( (lat: Double, lon: Double) -> Unit) ) {
        if (curActivity == null) {
            callBack.invoke(0.0, 0.0)
        } else {
            curActivity?.getLastLocation(callBack)
        }
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
}