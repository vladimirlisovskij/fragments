package com.example.myapplication.presenter.secondPage

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.sqlite.SQLiteModule
import com.example.myapplication.presenter.injectApplication.MainApplication
import com.example.myapplication.presenter.mainActivity.MainActivity
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class SecondPage : MvpAppCompatFragment(R.layout.fragment_second_page), SecondPageView {
    companion object {
        @JvmStatic
        fun newInstance() = SecondPage()
    }

    private lateinit var mainRecycler: RecyclerView
    private lateinit var adapter: SecondPageAdapter
    private lateinit var cityInput: EditText
    private lateinit var cityBut: Button
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var presenterProvider: Provider<SecondPagePresenter>
    private val presenter by moxyPresenter { presenterProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication
            .getInstance()
            .mainComponent
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainRecycler = view.findViewById(R.id.cityRecycler)
        cityInput = view.findViewById(R.id.sityInput)
        cityBut = view.findViewById(R.id.cityButton)
        progressBar = view.findViewById(R.id.progressBar)

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
        }

        presenter.getItems()
    }

    override fun setItems(strings: ArrayList<SQLiteModule.City>) {
        adapter.containerList = strings
    }

    override fun addItem(string: SQLiteModule.City) {
        adapter.addItem(string)
    }

    override fun showBar(state: Boolean) {
        progressBar.visibility = when(state) {
            true -> View.VISIBLE
            false -> View.INVISIBLE
        }
    }
}