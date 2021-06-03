package com.example.myapplication.presenter.secondPage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.sqlite.SQLiteModule

class SecondPageAdapter : RecyclerView.Adapter<SecondPageAdapter.ItemHolder>() {
    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityTV: TextView = itemView.findViewById(R.id.itemTV)
        private var City: SQLiteModule.City? = null

        fun setData(city: SQLiteModule.City) {
            City = city
            cityTV.text = City?.Name
        }

        init {
            cityTV.setOnClickListener {
                City?.let {
                    onClick?.invoke(it.ID.toString())
                }
            }
        }
    }

    var onClick: ( (String) -> Unit)? = null

    var containerList: ArrayList<SQLiteModule.City>? = null
        set (dataFormContainerList: ArrayList<SQLiteModule.City>?) {
            field = dataFormContainerList
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page2_recycler_item, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return containerList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        containerList?.let {
            holder.setData(it[position])
        }
    }

    fun addItem(city: SQLiteModule.City) {
        containerList?.let {
            val index = it.size
            it.add(city)
            notifyItemInserted(index)
            notifyItemRangeChanged(index, it.size)
            Log.d("tag", containerList?.size.toString())
        }
    }
}