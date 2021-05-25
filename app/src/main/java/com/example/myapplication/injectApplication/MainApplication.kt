package com.example.myapplication.injectApplication

import android.app.Activity
import android.app.Application
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.myapplication.mainActivity.MainActivity
import com.example.myapplication.mainComponent.DaggerMainComponent
import com.example.myapplication.mainComponent.MainComponent

class MainApplication : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        private lateinit var instance: MainApplication

        @JvmStatic
        fun getInstance() : MainApplication = instance
    }

    private var curActivity: MainActivity? = null
    private var mainComponent: MainComponent = DaggerMainComponent.create()

    fun getComponent() = mainComponent

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

    fun getLastLocation(callBack: ( (Location?) -> Unit) ) {
        if (curActivity == null) {
            callBack.invoke(null)
        } else {
            curActivity?.getLastLocation(callBack)
        }
    }

    fun makeToast(string: String) {
        Handler(this.mainLooper).post {
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
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