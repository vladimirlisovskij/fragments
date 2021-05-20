package com.example.myapplication.secondPage

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activityHolder.ActivityModule
import com.example.myapplication.contextHolder.ContextModule
import com.example.myapplication.mainActivity.MainActivity
import com.example.myapplication.mainComponent.DaggerMainComponent
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class SecondPage : MvpAppCompatFragment(R.layout.fragment_second_page), SecondPageView {

    private lateinit var mainRecycler: RecyclerView
    private lateinit var adapter: SecondPageAdapter
    private lateinit var cityInput: EditText
    private lateinit var cityBut: Button

    @InjectPresenter
    lateinit var presenter: SecondPagePresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainRecycler = view.findViewById(R.id.cityRecycler)
        cityInput = view.findViewById(R.id.sityInput)
        cityBut = view.findViewById(R.id.cityButton)

        adapter = SecondPageAdapter()
        mainRecycler.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        mainRecycler.adapter = adapter

        adapter.onClick = {
            presenter.setCity(Integer.valueOf(it))
            (activity as MainActivity).toFirst()
        }

        cityBut.setOnClickListener {
            val text: String = cityInput.text.toString()
            presenter.addItem(text)
            adapter.addItem(text)
        }

        DaggerMainComponent
            .builder()
            .contextModule(ContextModule(context!!))
            .activityModule(ActivityModule(activity!!))
            .build()
            .inject(presenter)

        presenter.getItems()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SecondPage()
    }

    override fun setItems(strings: ArrayList<String>) {
        adapter.containerList = strings
    }
}