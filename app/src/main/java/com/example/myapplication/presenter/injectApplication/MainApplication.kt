package com.example.myapplication.presenter.injectApplication

import android.app.Activity
import android.app.Application
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.myapplication.presenter.mainActivity.MainActivity
import com.example.myapplication.presenter.mainComponent.DaggerMainComponent
import com.example.myapplication.presenter.mainComponent.MainComponent

class MainApplication : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        private lateinit var instance: MainApplication

        @JvmStatic
        fun getInstance() : MainApplication = instance
    }

    private var curActivity: MainActivity? = null

    val mainComponent: MainComponent by lazy {
        DaggerMainComponent.create()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
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