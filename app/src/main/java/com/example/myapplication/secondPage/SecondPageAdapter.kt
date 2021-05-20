package com.example.myapplication.secondPage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class SecondPageAdapter : RecyclerView.Adapter<SecondPageAdapter.ItemHolder>() {
    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityTV: TextView = itemView.findViewById(R.id.itemTV)

        fun setData(cityName: String) {
            cityTV.text = cityName
        }

        init {
            cityTV.setOnClickListener({ onClick?.invoke(cityTV.text as String) })
        }
    }

    var onClick: ( (String) -> Unit)? = null

    var containerList: ArrayList<String>? = null
        set (dataFormContainerList: ArrayList<String>?) {
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

    fun addItem(cityName: String) {
        containerList?.let {
            val index = it.size
            it.add(cityName)
            notifyItemInserted(index)
            notifyItemRangeChanged(index, it.size)
            Log.d("tag", containerList?.size.toString())
        }
    }
}