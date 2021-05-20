package com.example.myapplication.firstPage

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.activityHolder.ActivityModule
import com.example.myapplication.contextHolder.ContextModule
import com.example.myapplication.mainComponent.DaggerMainComponent
import com.example.myapplication.retrofit.WeatherContainer
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class FirstPage : MvpAppCompatFragment(R.layout.fragment_first_page), FirstPageView {
    private lateinit var tempTV: TextView
    private lateinit var humTV: TextView
    private lateinit var wMain: TextView
    private lateinit var wDesc: TextView
    private lateinit var windTV: TextView
    private lateinit var ssetTV: TextView
    private lateinit var sriseTV: TextView
    private lateinit var timeTV: TextView
    private lateinit var nameTV: TextView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @InjectPresenter
    lateinit var presenter: FirstFragmentPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempTV = view.findViewById(R.id.tempTV)
        humTV = view.findViewById(R.id.humTV)
        wMain = view.findViewById(R.id.wMainTV)
        wDesc = view.findViewById(R.id.wDescTV)
        windTV = view.findViewById(R.id.windTV)
        ssetTV = view.findViewById(R.id.ssetTV)
        sriseTV = view.findViewById(R.id.sriseTV)
        timeTV = view.findViewById(R.id.curTime)
        nameTV = view.findViewById(R.id.name)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            presenter.refresh()
        }

        DaggerMainComponent
            .builder()
            .contextModule(ContextModule(context!!))
            .activityModule(ActivityModule(activity!!))
            .build()
            .inject(presenter)
    }

    override fun setInfo(container: WeatherContainer) {
        tempTV.text = container.tempStr
        humTV.text = container.humStr
        wMain.text = container.wMainStr
        wDesc.text = container.wDescStr
        windTV.text = container.windStr
        ssetTV.text = container.ssetStr
        sriseTV.text = container.sriseStr
        timeTV.text = container.timeStr
        nameTV.text = container.name
    }

    override fun startRefresh() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun stopRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance() = FirstPage()
    }
}