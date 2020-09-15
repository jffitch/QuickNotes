package com.mathgeniusguide.quicknotes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import kotlinx.android.synthetic.main.tag_item.view.*

class TagAdapter (private val items: List<String>, val act: MainActivity) : RecyclerView.Adapter<TagAdapter.ViewHolder> () {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(act.applicationContext).inflate(R.layout.tag_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        holder.tagName.text = i
        holder.tagChecked.isChecked = false
        holder.tagChecked.setOnClickListener {
            act.tagList
        }
    }

    class ViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        val tagName = view.tagName
        val tagChecked = view.tagChecked
        val parent = view
    }
}