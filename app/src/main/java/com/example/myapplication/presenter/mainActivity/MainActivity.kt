package com.example.myapplication.presenter.mainActivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.R
import com.example.myapplication.presenter.firstPage.FirstPage
import com.example.myapplication.presenter.injectApplication.MainApplication
import com.example.myapplication.presenter.secondPage.SecondPage
import com.example.myapplication.presenter.thirdPage.ThirdPageFragment
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class MainActivity
    : MvpAppCompatActivity(R.layout.activity_main)
    , MainView
{
    companion object {
        private const val FINE_LOC_CODE = 123

        private var isStart = false
    }

    private var isFineGranted = false
    private var isResponse = true

    private lateinit var bottomView: BottomNavigationView
    private lateinit var fragmentManager: FragmentManager

    private val navigator = object : AppNavigator(this, R.id.container) {

        override fun setupFragmentTransaction(
            screen: FragmentScreen,
            fragmentTransaction: FragmentTransaction,
            currentFragment: Fragment?,
            nextFragment: Fragment
        ) {
            if (nextFragment is FirstPage) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_l,
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.slide_out_l
                )
            } else if (nextFragment is ThirdPageFragment) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.slide_out
                )
            } else if (nextFragment is SecondPage) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.fade_in
                )
            }
        }
    }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() }

    fun getLastLocation(callBack: ( (Location?) -> Unit) ) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callBack.invoke(null)
            return
        }
        LocationServices
            .getFusedLocationProviderClient(this)
            .lastLocation.addOnSuccessListener(callBack)
    }

    fun getPermissions(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission, Manifest.permission.ACCESS_COARSE_LOCATION), requestCode)
        } else {
            isResponse = true
            isFineGranted = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOC_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                isResponse = true
                isFineGranted = false
                Toast.makeText(this, "fine location not enabled", Toast.LENGTH_SHORT).show()
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "fine location granted", Toast.LENGTH_SHORT).show()
                isResponse = true
                isFineGranted = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication
            .getInstance()
            .mainComponent
            .inject(this)
        setTheme(R.style.Theme_MyApplication)
        application.registerActivityLifecycleCallbacks(MainApplication.getInstance())
        super.onCreate(savedInstanceState)

        if (!isStart) {
            val content: View = findViewById(android.R.id.content)
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return if (isFineGranted) {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            presenter.openFirst()
                            true
                        } else {
                            if (isResponse) {
                                isResponse = false
                                getPermissions(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    FINE_LOC_CODE
                                )

                            }
                            false
                        }
                    }
                }
            )
            isStart = true
        }

        bottomView = findViewById(R.id.myMenu)

        bottomView.setItemOnTouchListener(R.id.page_1, View.OnTouchListener{ view: View, motionEvent: MotionEvent ->
            view.performClick()
            presenter.toFirst()
            true
        })

        bottomView.setItemOnTouchListener(R.id.page_2, View.OnTouchListener{ view: View, motionEvent: MotionEvent ->
            view.performClick()
            presenter.toSecond()
            true
        })

        bottomView.setItemOnTouchListener(R.id.page_3, View.OnTouchListener{ view: View, motionEvent: MotionEvent ->
            view.performClick()
            presenter.toThird()
            true
        })

        fragmentManager = supportFragmentManager
    }

    override fun onResume() {
        super.onResume()
        presenter.toSecond()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(this, "APP")
        super.onDestroy()
        application.unregisterActivityLifecycleCallbacks(MainApplication.getInstance())
    }

    fun toFirst() {
        presenter.openFirst()
    }

    override fun moveToFirst() {
        bottomView.selectedItemId = R.id.page_1
    }

    override fun moveToSecond() {
        bottomView.selectedItemId = R.id.page_2
    }

    override fun moveToThird() {
        bottomView.selectedItemId = R.id.page_3
    }

    override fun onBackPressed() {
        presenter.onBack()
    }
}