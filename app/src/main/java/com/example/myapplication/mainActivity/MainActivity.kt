package com.example.myapplication.mainActivity

import android.os.Bundle
import android.os.SystemClock.sleep
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.R
import com.example.myapplication.thirdPage.ThirdPageFragment
import com.example.myapplication.firstPage.FirstPage
import com.example.myapplication.secondPage.SecondPage
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainView
{
    private lateinit var bottomView: BottomNavigationView;
    private lateinit var fragmentManager: FragmentManager

    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyApplication)
        super.onCreate(savedInstanceState)

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        sleep(3000)
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                }
        )

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