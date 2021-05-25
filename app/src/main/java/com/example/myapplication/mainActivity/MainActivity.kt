package com.example.myapplication.mainActivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.R
import com.example.myapplication.firstPage.FirstPage
import com.example.myapplication.injectApplication.MainApplication
import com.example.myapplication.secondPage.SecondPage
import com.example.myapplication.thirdPage.ThirdPageFragment
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainView
{
    companion object {
        private const val FINE_LOC_CODE = 123

        private var isStart = false
    }

    private var isFineGranted = false
    private var isResponse = true

    private lateinit var bottomView: BottomNavigationView
    private lateinit var fragmentManager: FragmentManager

    @InjectPresenter
    lateinit var presenter: MainPresenter

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
                            presenter.iSReady()
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
        bottomView.setOnNavigationItemSelectedListener {
            item ->
            when(item.itemId)
            {
                R.id.page_1 -> {
                    presenter.toFirst()
                    true
                }
                R.id.page_2 -> {
                    presenter.toSecond()
                    true
                }
                R.id.page_3 -> {
                    presenter.toThird()
                    true
                }
                else -> false
            }
        }

        fragmentManager = supportFragmentManager
    }

    override fun onDestroy() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(this, "APP")
        super.onDestroy()
        application.unregisterActivityLifecycleCallbacks(MainApplication.getInstance())
    }

    fun toFirst() {
        bottomView.selectedItemId = R.id.page_1
    }

    override fun showFirstPage() {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pageFrag, FirstPage.newInstance())
        fragmentTransaction.commit()
    }

    override fun showSecondPage() {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pageFrag, SecondPage.newInstance())
        fragmentTransaction.commit()
    }

    override fun showThirdPage() {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pageFrag, ThirdPageFragment.newInstance())
        fragmentTransaction.commit()
    }
}