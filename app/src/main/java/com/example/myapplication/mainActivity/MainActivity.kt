package com.example.myapplication.mainActivity

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.R
import com.example.myapplication.firstPage.FirstPage
import com.example.myapplication.injectApplication.InjectApplication
import com.example.myapplication.secondPage.SecondPage
import com.example.myapplication.thirdPage.ThirdPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainView
{
    private lateinit var bottomView: BottomNavigationView
    private lateinit var fragmentManager: FragmentManager

    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onDestroy() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(InjectApplication.getInstance().getMainComponent(), "COMPONENT")
        appWatcher.expectWeaklyReachable(this, "APP")
        application.unregisterActivityLifecycleCallbacks(InjectApplication.getInstance())
        super.onDestroy()
    }


    companion object {
        const val FINE_LOC_CODE = 123
        const val COARSE_LOC_CODE = 456

        private var isStart = false
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOC_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "fine location not enabled", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == COARSE_LOC_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "coarse location not enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyApplication)
        application.registerActivityLifecycleCallbacks(InjectApplication.getInstance())
        super.onCreate(savedInstanceState)


        if (!isStart) {
            val content: View = findViewById(android.R.id.content)
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        SystemClock.sleep(2000)
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
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

    fun toFirst() {
        presenter.toFirst()
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